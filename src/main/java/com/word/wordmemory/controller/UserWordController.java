package com.word.wordmemory.controller;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.service.UserWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user-words")
public class UserWordController {

    @Autowired
    private UserWordService userWordService;

    @PutMapping("/{wordId}/status")
    public Result<Void> updateStatus(
            @PathVariable Long wordId,
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Integer> body) {
        Integer wordStatus = body.get("wordStatus");
        if (wordStatus == null || wordStatus < 0 || wordStatus > 2) {
            return Result.fail(400, "wordStatus \u65e0\u6548\uff0c\u53d6\u503c\u8303\u56f4 0-2");
        }
        userWordService.updateWordStatus(userId, wordId, wordStatus);
        return Result.success();
    }
}
