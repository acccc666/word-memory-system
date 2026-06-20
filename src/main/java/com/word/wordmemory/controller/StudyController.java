package com.word.wordmemory.controller;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.vo.StudySubmitRequest;
import com.word.wordmemory.service.UserWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/study")
public class StudyController {

    @Autowired
    private UserWordService userWordService;

    /**
     * 提交学习结果（用户中途退出时调用）
     * - "remembered" → 已记住(2)
     * - "fuzzy" / "forgot" / "wrong" → 未记住(0)，遗忘次数 +1
     * - 未在 results 中的单词 → 状态不变
     */
    @PostMapping("/submit")
    public Result<Void> submit(
            @RequestAttribute("userId") Long userId,
            @RequestBody StudySubmitRequest request) {
        if (request.getResults() == null || request.getResults().isEmpty()) {
            return Result.success();
        }
        userWordService.batchUpdateStudyResults(userId, request.getResults());
        return Result.success();
    }
}