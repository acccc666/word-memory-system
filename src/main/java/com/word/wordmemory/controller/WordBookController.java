package com.word.wordmemory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.WordBook;
import com.word.wordmemory.entity.vo.WordWithStatusVO;
import com.word.wordmemory.service.WordBookService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/word-books")
public class WordBookController {

    @Autowired
    private WordBookService wordBookService;
    @Autowired
    private WordService wordService;

    @GetMapping
    public Result<IPage<WordBook>> list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "targetUser", required = false) String targetUser) {
        return Result.success(wordBookService.getWordBooks(page, size, targetUser));
    }

    @GetMapping("/{bookId}/words")
    public Result<IPage<WordWithStatusVO>> words(
            @PathVariable(name = "bookId") Long bookId,
            @RequestAttribute("userId") Long userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return Result.success(wordService.getWordsWithStatusPage(bookId, userId, page, size));
    }

    @GetMapping("/{bookId}/study")
    public Result<java.util.List<WordWithStatusVO>> study(
            @PathVariable(name = "bookId") Long bookId,
            @RequestAttribute("userId") Long userId,
            @RequestParam(name = "count", defaultValue = "20") int needCount) {
        return Result.success(wordService.getStudyWords(bookId, userId, needCount));
    }
}