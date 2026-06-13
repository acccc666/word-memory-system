package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.ExamRecord;
import com.word.wordmemory.mapper.ExamRecordMapper;
import com.word.wordmemory.service.ExamRecordService;
import org.springframework.stereotype.Service;

@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {
}
