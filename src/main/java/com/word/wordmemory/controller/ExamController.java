package com.word.wordmemory.controller;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    // 开始考试
    @PostMapping("/start")
    public Result<Map<String, Object>> start(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Object> params) {
        Long bookId = Long.valueOf(params.get("bookId").toString());
        int questionCount = Integer.parseInt(params.getOrDefault("questionCount", "10").toString());
        int setTime = Integer.parseInt(params.getOrDefault("setTime", "10").toString());
        return Result.success(examService.startExam(userId, bookId, questionCount, setTime));
    }

    // 提交答卷
    @PostMapping("/{examId}/submit")
    public Result<Map<String, Object>> submit(
            @RequestAttribute("userId") Long userId,
            @PathVariable(name = "examId") Long examId,
            @RequestBody Map<String, Object> body) {
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
        return Result.success(examService.submitExam(userId, examId, answers));
    }

    // 考试记录
    @GetMapping("/records")
    public Result<List<ExamRecord>> records(
            @RequestAttribute("userId") Long userId,
            @RequestParam(name = "bookId", required = false) Long bookId) {
        return Result.success(examService.getRecords(userId, bookId));
    }
}