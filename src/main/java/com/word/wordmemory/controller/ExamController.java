package com.word.wordmemory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.word.wordmemory.algorithm.ExamService;
import com.word.wordmemory.algorithm.QuizQuestionData;
import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.DTO.StartExamDTO;
import com.word.wordmemory.DTO.SubmitExamDTO;
import com.word.wordmemory.entity.ExamRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    @PostMapping("/start")
    public Result<List<QuizQuestionData>> startExam(
            @RequestAttribute("userId") Long userId,
            @RequestBody StartExamDTO dto) {
        if (dto.getEnToZhRatio() < 0 || dto.getEnToZhRatio() > 1) {
            return Result.fail(400, "题型比例必须在 0.0 到 1.0 之间");
        }
        List<QuizQuestionData> examPaper = examService.startExam(
                userId, dto.getBookId(), dto.getExamCount(), dto.getEnToZhRatio());
        return Result.success(examPaper);
    }

    @PostMapping("/submit")
    public Result<Void> submitExam(
            @RequestAttribute("userId") Long userId,
            @RequestBody SubmitExamDTO dto) {
        examService.submitExam(userId, dto.getBookId(), dto.getScore());
        return Result.success();
    }

    @GetMapping("/records")
    public Result<IPage<ExamRecord>> records(
            @RequestAttribute("userId") Long userId,
            @RequestParam(name = "bookId", required = false) Long bookId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return Result.success(examService.getRecords(userId, bookId, page, size));
    }
}