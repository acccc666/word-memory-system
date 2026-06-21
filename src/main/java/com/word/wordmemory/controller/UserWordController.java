package com.word.wordmemory.controller;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.service.UserWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 用户单词状态控制器 —— 单个修改单词记忆状态
 */
@RestController
@RequestMapping("/user-words")
public class UserWordController {

    @Autowired
    private UserWordService userWordService;

    /**
     * 修改单词记忆状态
     * PUT /user-words/{wordId}/status
     * Body: { "wordStatus": 2 }
     * wordStatus: 0=未记住  1=模糊  2=已记住
     */
    @PutMapping("/{wordId}/status")
    public Result<Void> updateStatus(
            @PathVariable(name = "wordId") Long wordId,
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, Integer> body) {

        Integer wordStatus = body.get("wordStatus");
        // 校验取值范围 0-2
        if (wordStatus == null || wordStatus < 0 || wordStatus > 2) {
            return Result.fail(400, "wordStatus 无效，取值范围 0-2");
        }

        userWordService.updateWordStatus(userId, wordId, wordStatus);
        return Result.success();
    }
}
