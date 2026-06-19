package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private RandomWordService randomWordService;
    @Autowired
    private WordService wordService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成试卷并存入 Redis (开始考试)
     */
    public List<QuizQuestionData> startExam(Long userId, Long bookId, int examCount, double enToZhRatio) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;

        // 1. 检查 Redis 是否有未完成的试卷（断点续考逻辑）
        if (redisTemplate.hasKey(redisKey)) {
            return (List<QuizQuestionData>) redisTemplate.opsForValue().get(redisKey);
        }

        // 2. 如果没有，从数据库随机抽取单词出题
        List<Word> allBookWords = wordService.lambdaQuery().eq(Word::getBookId, bookId).list();
        List<String> allMeanings = allBookWords.stream().map(Word::getChinese).collect(Collectors.toList());
        List<String> allEnglish = allBookWords.stream().map(Word::getEnglish).collect(Collectors.toList());

        Collections.shuffle(allBookWords);
        int count = Math.min(examCount, allBookWords.size());
        List<Word> targetWords = allBookWords.subList(0, count);

        List<QuizQuestionData> examPaper = new ArrayList<>();
        Random random = new Random();

        // 3. 组装每道题
        for (Word targetWord : targetWords) {
            boolean isEnToZh = random.nextDouble() < enToZhRatio;
            QuizQuestionData question = randomWordService.generateSingleQuiz(targetWord, allMeanings, allEnglish, isEnToZh);
            examPaper.add(question);
        }

        // 4. 存入 Redis，设置 2 小时过期
        redisTemplate.opsForValue().set(redisKey, examPaper, 2, TimeUnit.HOURS);
        return examPaper;
    }

    /**
     * 提交试卷结束考试
     */
    public void submitExam(Long userId, Long bookId, int score) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        redisTemplate.delete(redisKey);
    }
}