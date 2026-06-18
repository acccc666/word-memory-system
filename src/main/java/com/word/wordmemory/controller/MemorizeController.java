package com.word.wordmemory.controller;

import com.word.wordmemory.algorithm.RandomWordService;
import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专门负责“背单词（学习）”模块的接口
 */
@RestController
@RequestMapping("/memorize")
public class MemorizeController {

    @Autowired
    private RandomWordService randomWordService;

    /**
     * 获取今日背单词列表
     * API: GET /memorize/list?bookId=1&needCount=20
     *
     * @param bookId    前端传来的单词书ID
     * @param userId    从拦截器中获取的当前登录用户ID
     * @param needCount 需要抽取的单词数量（默认20）
     * @return 经过时间衰减算法处理后的单词列表
     */
    @GetMapping("/list")
    public Result<List<Word>> getMemorizeList(
            @RequestParam Long bookId,
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") Integer needCount) {

        // 调用我们精心打造的艾宾浩斯时间衰减算法引擎，抽出单词列表
        List<Word> words = randomWordService.generateDailyWordList(userId, bookId, needCount);

        return Result.success(words);
    }
}
