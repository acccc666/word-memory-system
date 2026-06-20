package com.word.wordmemory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.ExamRecord;

public interface  ExamRecordService extends IService <ExamRecord > {
    // 建议加上，表示重写了 ExamRecordService 接口的方法
    Page<ExamRecord> getExamHistory(Long userId, Integer pageNum, Integer pageSize);
}
