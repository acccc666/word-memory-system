package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.word.wordmemory.entity.Word;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WordMapper extends BaseMapper <Word>{
}
