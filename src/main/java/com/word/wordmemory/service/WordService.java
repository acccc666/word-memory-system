package com.word.wordmemory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.entity.vo.WordWithStatusVO;

import java.util.List;

public interface WordService extends IService<Word> {
    List<WordWithStatusVO> getWordsWithStatus(Long bookId, Long userId);
    IPage<WordWithStatusVO> getWordsWithStatusPage(Long bookId, Long userId, int page, int size);
    List<WordWithStatusVO> getStudyWords(Long bookId, Long userId, int needCount);
}