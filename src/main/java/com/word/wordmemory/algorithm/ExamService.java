package com.word.wordmemory.algorithm; // 注意：建议把它从 algorithm 挪到 service 包下

import com.word.wordmemory.algorithm.QuizQuestionData;
import com.word.wordmemory.algorithm.RandomWordService;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.mapper.ExamRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ExamService {

    // 🌟 注入咱们专门负责考试记录的 Mapper
    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private RandomWordService randomWordService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 假设你有 Mapper 可以查出书里所有的单词
    // @Autowired
    // private WordMapper wordMapper;

    /**
     * 生成试卷并存入 Redis (开始考试)
     */
    @SuppressWarnings("unchecked") // 消除强转 List 的黄色警告
    public List<QuizQuestionData> startExam(Long userId, Long bookId, int examCount, double enToZhRatio) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;

        // 1. 检查 Redis 是否有未完成的试卷（断点续考逻辑）
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            // 发现暂存记录，直接返回之前的试卷
            return (List<QuizQuestionData>) redisTemplate.opsForValue().get(redisKey);
        }

        // 2. 如果没有，开始生成新试卷
        // 先用你的时间衰减引擎，挑出最需要考的 N 个词
        List<Word> targetWords = randomWordService.generateDailyWordList(userId, bookId, examCount);

        // 获取全书单词库用于抽干扰项 (替换为真实 Mapper 调用)
        // List<Word> allBookWords = wordMapper.selectList(new QueryWrapper<Word>().eq("book_id", bookId));
        List<Word> allBookWords = new ArrayList<>(); // 伪代码占位

        List<QuizQuestionData> examPaper = new ArrayList<>();
        Random random = new Random();

        // 3. 组装每道题，并控制英/中比例
        for (Word targetWord : targetWords) {
            // 比如 ratio 是 0.7，那么随机数 < 0.7 时就是英译中
            boolean isEnToZh = random.nextDouble() < enToZhRatio;
            QuizQuestionData question = randomWordService.generateSingleQuiz(targetWord, allBookWords, isEnToZh);
            examPaper.add(question);
        }

        // 4. 存入 Redis，设置 2 小时过期（防内存泄漏）
        redisTemplate.opsForValue().set(redisKey, examPaper, 2, TimeUnit.HOURS);

        return examPaper;
    }

    /**
     * 提交试卷结束考试
     */
    public void submitExam(Long userId, Long bookId, int score) {

        // 🌟 1. 实现真正的保存分数逻辑！
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setScore(score);
        record.setCreateTime(LocalDateTime.now()); // 记录当前交卷时间

        // 调用 Mapper，把这条成绩记录真正写进 MySQL 的 exam_record 表里
        examRecordMapper.insert(record);

        // 🌟 2. 考试结束，清理 Redis 里的临时会话
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        redisTemplate.delete(redisKey);
    }
}