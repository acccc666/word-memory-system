package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.mapper.WordMapper;
import com.word.wordmemory.service.WordService;
import org.springframework.stereotype.Service;

@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {
}
