package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.mapper.ExamRecordMapper;
import com.word.wordmemory.service.ExamRecordService;
import org.springframework.stereotype.Service;

@Service // 🌟 修改1：@Service 必须放在类名上面
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Override
    public Page<ExamRecord> getExamHistory(Long userId, Integer pageNum, Integer pageSize) {
        // 兜底处理：防止前端未传参导致空指针异常，或传入非法值导致分页异常
        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        // 1. 创建分页对象 (当前页, 每页大小)
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件：查当前用户的记录，并且按照创建时间降序（最新的在最上面）
        LambdaQueryWrapper<ExamRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExamRecord::getUserId, userId)
                .orderByDesc(ExamRecord::getCreateTime);

        // 3. 🌟 修改2：执行分页查询，使用 ServiceImpl 自动提供的 baseMapper
        return baseMapper.selectPage(page, queryWrapper);
    }
}