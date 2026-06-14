package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.WordBook;
import com.word.wordmemory.mapper.WordBookMapper;
import com.word.wordmemory.service.WordBookService;
import org.springframework.stereotype.Service;

@Service
public class WordBookServiceImpl extends ServiceImpl<WordBookMapper, WordBook> implements WordBookService {

    @Override
    public IPage<WordBook> getWordBooks(int page, int size, String targetUser) {
        Page<WordBook> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<WordBook> wrapper = new LambdaQueryWrapper<>();
        if (targetUser != null && !targetUser.trim().isEmpty()) {
            wrapper.eq(WordBook::getTargetUser, targetUser);
        }
        wrapper.orderByDesc(WordBook::getCreateTime);
        return page(pageObj, wrapper);
    }
}
