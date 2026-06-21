package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.WordBook;
import com.word.wordmemory.mapper.WordBookMapper;
import com.word.wordmemory.service.WordBookService;
import org.springframework.stereotype.Service;

/**
 * 单词书 Service 实现 —— 分页查询
 */
@Service
public class WordBookServiceImpl extends ServiceImpl<WordBookMapper, WordBook>
        implements WordBookService {

    /**
     * 分页查询单词书（可选按 targetUser 过滤，按创建时间倒序）
     */
    @Override
    public IPage<WordBook> getWordBooks(int page, int size, String targetUser) {
        Page<WordBook> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<WordBook> wrapper = new LambdaQueryWrapper<>();

        // 按目标用户过滤（如 "四级"、"六级"），不传则查全部
        if (targetUser != null && !targetUser.trim().isEmpty()) {
            wrapper.eq(WordBook::getTargetUser, targetUser);
        }

        // 按创建时间倒序（最新创建的在最前面）
        wrapper.orderByDesc(WordBook::getCreateTime);

        // MyBatis-Plus 自动分页：COUNT + LIMIT
        return page(pageObj, wrapper);
    }
}
