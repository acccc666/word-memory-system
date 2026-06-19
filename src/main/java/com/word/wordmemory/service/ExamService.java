package com.word.wordmemory.service;

import com.word.wordmemory.entity.ExamRecord;
import java.util.List;
import java.util.Map;

public interface ExamService {
    Map<String, Object> startExam(Long userId, Long bookId, int questionCount, int setTime);
    Map<String, Object> submitExam(Long userId, Long examId, List<Map<String, Object>> answers);
    List<ExamRecord> getRecords(Long userId, Long bookId);
}