package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.word.wordmemory.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    // 继承 BaseMapper 后，不需要手写任何 SQL，直接拥有 insert、selectPage 等方法
}