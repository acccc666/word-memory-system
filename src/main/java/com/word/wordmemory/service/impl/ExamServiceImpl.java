package com.word.wordmemory.service.impl;

import com.word.wordmemory.algorithm.QuizQuestionData;
import com.word.wordmemory.algorithm.RandomWordService;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.service.ExamRecordService;
import com.word.wordmemory.service.ExamService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import com.word.wordmemory.entity.vo.ExamSubmitRequest;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private WordService wordService;
    @Autowired
    private ExamRecordService examRecordService;
    @Autowired
    private RandomWordService randomWordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> startExam(Long userId, Long bookId, int questionCount, int setTime) {
        // 1. 获取该本书所有单词的中文释义（用于出题的干扰项）
        List<Word> allWords = wordService.lambdaQuery().eq(Word::getBookId, bookId).list();
        List<String> allMeanings = allWords.stream()
                .map(Word::getChinese)
                .collect(Collectors.toList());

        // 2. 随机抽取 questionCount 个单词作为考题
        Collections.shuffle(allWords);
        int count = Math.min(questionCount, allWords.size());
        List<Word> examWords = allWords.subList(0, count);

        // 3. 为每个单词生成一道选择题
        List<QuizQuestionData> questions = new ArrayList<>();
        for (Word w : examWords) {
            QuizQuestionData q = randomWordService.generateSingleQuiz(w, allMeanings);
            if (q != null) {
                questions.add(q);
            }
        }

        // 4. 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setExamNum(questions.size());
        record.setScore(0);
        record.setSetTime(setTime);
        record.setExamStatus(0); // 0=进行中
        record.setCreateTime(LocalDateTime.now());
        examRecordService.save(record);

        // 5. 组装返回
        Map<String, Object> result = new HashMap<>();
        result.put("examId", record.getId());
        result.put("questions", questions);
        result.put("total", questions.size());
        result.put("setTime", setTime);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitExam(Long userId, Long examId, List<ExamSubmitRequest.Answer> answers) {
        // 1. 查找考试记录
        ExamRecord record = examRecordService.getById(examId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new RuntimeException("考试记录不存在");
        }
        if (record.getExamStatus() == 1) {
            throw new RuntimeException("考试已提交");
        }

        // 2. 逐题判分
        int correct = 0;
        int total = answers.size();
        List<Map<String, Object>> details = new ArrayList<>();

        for (ExamSubmitRequest.Answer answer : answers) {
            Long wordId = answer.getWordId();
            String selected = answer.getSelectedAnswer();

            // 从数据库获取正确答案
            Word word = wordService.getById(wordId);
            boolean isCorrect = word != null && word.getChinese().equals(selected);

            Map<String, Object> detail = new HashMap<>();
            detail.put("wordId", wordId);
            detail.put("english", word != null ? word.getEnglish() : "");
            detail.put("correctAnswer", word != null ? word.getChinese() : "");
            detail.put("yourAnswer", selected);
            detail.put("correct", isCorrect);
            details.add(detail);

            if (isCorrect) correct++;
        }

        // 3. 计算得分（百分制）
        int score = total > 0 ? (int) Math.round((double) correct / total * 100) : 0;

        // 4. 更新考试记录
        record.setScore(score);
        record.setExamStatus(1); // 1=已完成
        record.setEndTime(LocalDateTime.now());
        examRecordService.updateById(record);

        // 5. 组装返回
        Map<String, Object> result = new HashMap<>();
        result.put("examId", examId);
        result.put("score", score);
        result.put("total", total);
        result.put("correct", correct);
        result.put("wrong", total - correct);
        result.put("details", details);
        return result;
    }

    @Override
    public List<ExamRecord> getRecords(Long userId, Long bookId) {
        if (bookId != null) {
            return examRecordService.lambdaQuery()
                    .eq(ExamRecord::getUserId, userId)
                    .eq(ExamRecord::getBookId, bookId)
                    .orderByDesc(ExamRecord::getCreateTime)
                    .list();
        }
        return examRecordService.lambdaQuery()
                .eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreateTime)
                .list();
    }
}