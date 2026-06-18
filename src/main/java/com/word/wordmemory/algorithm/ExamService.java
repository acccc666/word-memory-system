package com.word.wordmemory.algorithm;

import com.word.wordmemory.algorithm.RandomWordService;
import com.word.wordmemory.entity.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ExamService {

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
    public List<QuizQuestionData> startExam(Long userId, Long bookId, int examCount, double enToZhRatio) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;

        // 1. 检查 Redis 是否有未完成的试卷（断点续考逻辑）
        if (redisTemplate.hasKey(redisKey)) {
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
        // 1. 把分数保存到 MySQL 的 test_record 表...

        // 2. 考试结束，清理 Redis 里的临时会话
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        redisTemplate.delete(redisKey);
    }
}