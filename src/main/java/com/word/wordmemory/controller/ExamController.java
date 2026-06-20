package com.word.wordmemory.controller; // 注意替换为你们的包名

import com.word.wordmemory.algorithm.ExamService;
import com.word.wordmemory.algorithm.QuizQuestionData;
import com.word.wordmemory.common.result.Result; // 根据你们实际的Result路径修改
import com.word.wordmemory.DTO.StartExamDTO;
import com.word.wordmemory.DTO.SubmitExamDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专门负责“单词自测（考试）”模块的接口
 */
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private ExamService examService;

    /**
     * 1. 开始考试（含断点续考逻辑）
     * API: POST /exam/start
     */
    @PostMapping("/start")
    public Result<List<QuizQuestionData>> startExam(
            @RequestAttribute("userId") Long userId,
            @RequestBody StartExamDTO dto) {

        // 参数校验（防止前端传乱七八糟的比例过来）
        if (dto.getEnToZhRatio() < 0 || dto.getEnToZhRatio() > 1) {
            return Result.fail(400, "题型比例必须在 0.0 到 1.0 之间");
        }

        // 呼叫 Service 层的 Redis 引擎去拿试卷
        List<QuizQuestionData> examPaper = examService.startExam(
                userId,
                dto.getBookId(),
                dto.getExamCount(),
                dto.getEnToZhRatio()
        );

        return Result.success(examPaper);
    }

    /**
     * 2. 提交试卷并记录分数
     * API: POST /exam/submit
     */
    @PostMapping("/submit")
    public Result<Void> submitExam(
            @RequestAttribute("userId") Long userId,
            @RequestBody SubmitExamDTO dto) {

        // 呼叫 Service 层，把分数记录到数据库，并销毁 Redis 里的临时试卷
        examService.submitExam(userId, dto.getBookId(), dto.getScore());

        return Result.success();
    }
}