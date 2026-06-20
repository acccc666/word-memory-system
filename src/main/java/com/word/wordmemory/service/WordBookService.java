package com.word.wordmemory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.WordBook;

public interface WordBookService extends IService<WordBook> {
    IPage<WordBook> getWordBooks(int page, int size, String targetUser);
}