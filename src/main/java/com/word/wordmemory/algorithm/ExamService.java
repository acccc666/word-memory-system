package com.word.wordmemory.algorithm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.service.ExamRecordService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private ExamRecordService examRecordService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<QuizQuestionData> startExam(Long userId, Long bookId, int examCount, double enToZhRatio) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        if (redisTemplate.hasKey(redisKey)) {
            return (List<QuizQuestionData>) redisTemplate.opsForValue().get(redisKey);
        }
        List<Word> allBookWords = wordService.lambdaQuery().eq(Word::getBookId, bookId).list();
        List<String> allMeanings = allBookWords.stream().map(Word::getChinese).collect(Collectors.toList());
        List<String> allEnglish = allBookWords.stream().map(Word::getEnglish).collect(Collectors.toList());
        Collections.shuffle(allBookWords);
        int count = Math.min(examCount, allBookWords.size());
        List<Word> targetWords = allBookWords.subList(0, count);
        List<QuizQuestionData> examPaper = new ArrayList<>();
        Random random = new Random();
        for (Word targetWord : targetWords) {
            boolean isEnToZh = random.nextDouble() < enToZhRatio;
            QuizQuestionData question = randomWordService.generateSingleQuiz(targetWord, allMeanings, allEnglish, isEnToZh);
            examPaper.add(question);
        }
        redisTemplate.opsForValue().set(redisKey, examPaper, 2, TimeUnit.HOURS);
        return examPaper;
    }

    public void submitExam(Long userId, Long bookId, Integer score) {
        String redisKey = "exam_session:user:" + userId + ":book:" + bookId;
        redisTemplate.delete(redisKey);

        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setExamNum(0);
        record.setScore(score != null ? score : 0);
        record.setSetTime(0);
        record.setExamStatus(1);
        record.setCreateTime(LocalDateTime.now());
        record.setEndTime(LocalDateTime.now());
        examRecordService.save(record);
    }

    public IPage<ExamRecord> getRecords(Long userId, Long bookId, int page, int size) {
        Page<ExamRecord> pageObj = new Page<>(page, size);
        if (bookId != null) {
            return examRecordService.lambdaQuery()
                    .eq(ExamRecord::getUserId, userId)
                    .eq(ExamRecord::getBookId, bookId)
                    .orderByDesc(ExamRecord::getEndTime)
                    .page(pageObj);
        }
        return examRecordService.lambdaQuery()
                .eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getEndTime)
                .page(pageObj);
    }
}