package com.word.wordmemory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.WordBook;
import com.word.wordmemory.entity.vo.WordWithStatusVO;
import com.word.wordmemory.service.WordBookService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单词书控制器 —— 单词书列表 / 单词查询 / 学习调度 / 统计
 */
@RestController
@RequestMapping("/word-books")
public class WordBookController {

    @Autowired
    private WordBookService wordBookService;
    @Autowired
    private WordService wordService;

    /**
     * 单词书列表（分页，可筛选目标用户）
     * GET /word-books?page=1&size=10&targetUser=四级
     */
    @GetMapping
    public Result<IPage<WordBook>> list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "targetUser", required = false) String targetUser) {
        return Result.success(wordBookService.getWordBooks(page, size, targetUser));
    }

    /**
     * 查询某本书的所有单词（带每个单词的记忆状态）
     * GET /word-books/{bookId}/words
     */
    @GetMapping("/{bookId}/words")
    public Result<List<WordWithStatusVO>> words(
            @PathVariable(name = "bookId") Long bookId,
            @RequestAttribute("userId") Long userId) {
        return Result.success(wordService.getWordsWithStatus(bookId, userId));
    }

    /**
     * 获取学习单词列表（按记忆状态调度）
     * GET /word-books/{bookId}/study?count=20
     */
    @GetMapping("/{bookId}/study")
    public Result<List<WordWithStatusVO>> study(
            @PathVariable(name = "bookId") Long bookId,
            @RequestAttribute("userId") Long userId,
            @RequestParam(name = "count", defaultValue = "20") int needCount) {
        return Result.success(wordService.getStudyWords(bookId, userId, needCount));
    }

    /**
     * 统计该书的单词背诵进度
     * GET /word-books/{bookId}/stats
     */
    @GetMapping("/{bookId}/stats")
    public Result<Map<String, Object>> getStats(
            @PathVariable(name = "bookId") Long bookId,
            @RequestAttribute("userId") Long userId) {
        List<WordWithStatusVO> words = wordService.getWordsWithStatus(bookId, userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", words.size());
        stats.put("learned", words.stream().filter(w -> w.getWordStatus() == 2).count());
        stats.put("notRemembered", words.stream().filter(w -> w.getWordStatus() == 0).count());
        stats.put("fuzzy", words.stream().filter(w -> w.getWordStatus() == 1).count());
        return Result.success(stats);
    }
}
