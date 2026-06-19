package com.word.wordmemory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.entity.vo.WordWithStatusVO;

import java.util.List;

public interface WordService extends IService<Word> {
    List<WordWithStatusVO> getWordsWithStatus(Long bookId, Long userId);
    List<WordWithStatusVO> getStudyWords(Long bookId, Long userId, int needCount);
}
