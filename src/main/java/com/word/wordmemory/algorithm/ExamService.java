package com.word.wordmemory.algorithm; // 根据你的实际包名调整

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.word.wordmemory.algorithm.QuizQuestionData;
import com.word.wordmemory.algorithm.RandomWordService;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.service.ExamRecordService;
import com.word.wordmemory.service.UserWordService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private RandomWordService randomWordService;
    @Autowired
    private WordService wordService;
    @Autowired
    private ExamRecordService examRecordService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 🌟 新增：注入用户单词状态服务，用于获取用户的历史背诵/复习记录
    @Autowired
    private UserWordService userWordService;

    @SuppressWarnings("unchecked")
    public List<QuizQuestionData> startExam(Long userId, Long bookId, int examCount, double enToZhRatio) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            // 命中缓存，触发断点续考
            return (List<QuizQuestionData>) redisTemplate.opsForValue().get(redisKey);
        }

        // 1. 获取全书单词库及干扰项字典
        List<Word> allBookWords = wordService.lambdaQuery().eq(Word::getBookId, bookId).list();
        List<String> allMeanings = allBookWords.stream().map(Word::getChinese).collect(Collectors.toList());
        List<String> allEnglish = allBookWords.stream().map(Word::getEnglish).collect(Collectors.toList());

        // 2. 获取当前用户背诵过的单词状态记录，并转为 Map 加速查询 (WordID -> 记录)
        List<UserWord> userWords = userWordService.lambdaQuery().eq(UserWord::getUserId, userId).list();
        Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(UserWord::getWordId, uw -> uw, (v1, v2) -> v2));

        LocalDateTime now = LocalDateTime.now();

        // 3.  核心引擎替换：艾宾浩斯时间衰减动态排序
        List<Word> targetWords = allBookWords.stream()
                .sorted((w1, w2) -> {
                    UserWord uw1 = userWordMap.get(w1.getId());
                    UserWord uw2 = userWordMap.get(w2.getId());

                    // 分别计算两个单词当前的“饥渴度”权重得分
                    double score1 = calculateDecayScore(uw1, now);
                    double score2 = calculateDecayScore(uw2, now);

                    // 按照得分从高到低倒序排（分数越高的优先进入考卷）
                    return Double.compare(score2, score1);
                })
                .limit(examCount) // 截取权重最高的前 N 个单词
                .collect(Collectors.toList());

        // 4. 组装试卷
        List<QuizQuestionData> examPaper = new ArrayList<>();
        Random random = new Random();
        for (Word targetWord : targetWords) {
            boolean isEnToZh = random.nextDouble() < enToZhRatio;
            QuizQuestionData question = randomWordService.generateSingleQuiz(targetWord, allMeanings, allEnglish, isEnToZh);
            examPaper.add(question);
        }

        // 5. 存入 Redis 保障断点续考
        redisTemplate.opsForValue().set(redisKey, examPaper, 2, TimeUnit.HOURS);
        return examPaper;
    }

    /**
     * 艾宾浩斯动态权重得分计算器
     */
    private double calculateDecayScore(UserWord uw, LocalDateTime now) {
        // 如果是从来没背过的纯生词，赋予极高的基础分，强制优先出题
        if (uw == null || uw.getLastReviewTime() == null) {
            return 10000.0;
        }

        // ΔT: 距离上次复习经过了多少个小时
        long hoursSinceLast = Math.max(0, ChronoUnit.HOURS.between(uw.getLastReviewTime(), now));

        // 获取错误次数
        int forgetCount = uw.getForgetCount() != null ? uw.getForgetCount() : 0;

        // PPT 核心公式: Score = S_base + ΔT * α
        // 这里 S_base = forgetCount * 10
        // 衰减系数 α = 1.5 (每过1小时，出题概率增加 1.5)
        return (forgetCount * 10) + (hoursSinceLast * 1.5);
    }

    public void submitExam(Long userId, Long bookId, Integer score) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        redisTemplate.delete(redisKey);

        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setScore(score != null ? score : 0);
        record.setCreateTime(LocalDateTime.now());
        examRecordService.save(record);
    }

    public IPage<ExamRecord> getRecords(Long userId, Long bookId, int page, int size) {
        Page<ExamRecord> pageObj = new Page<>(page, size);
        if (bookId != null) {
            return examRecordService.lambdaQuery()
                    .eq(ExamRecord::getUserId, userId)
                    .eq(ExamRecord::getBookId, bookId)
                    .orderByDesc(ExamRecord::getCreateTime)
                    .page(pageObj);
        }
        return examRecordService.lambdaQuery()
                .eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreateTime)
                .page(pageObj);
    }
}