package com.word.wordmemory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.WordBook;
import com.word.wordmemory.entity.vo.WordWithStatusVO;
import com.word.wordmemory.service.WordBookService;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/word-books")
public class WordBookController {

    @Autowired
    private WordBookService wordBookService;
    @Autowired
    private WordService wordService;

    @GetMapping
    public Result<IPage<WordBook>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String targetUser) {
        return Result.success(wordBookService.getWordBooks(page, size, targetUser));
    }

    @GetMapping("/{bookId}/words")
    public Result<List<WordWithStatusVO>> words(
            @PathVariable Long bookId,
            @RequestAttribute("userId") Long userId) {
        return Result.success(wordService.getWordsWithStatus(bookId, userId));
    }
}
