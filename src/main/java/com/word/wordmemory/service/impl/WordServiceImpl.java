package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.entity.vo.WordWithStatusVO;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.mapper.WordMapper;
import com.word.wordmemory.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {

    @Autowired
    private UserWordMapper userWordMapper;

    @Override
    public List<WordWithStatusVO> getWordsWithStatus(Long bookId, Long userId) {
        List<Word> words = lambdaQuery().eq(Word::getBookId, bookId).list();
        if (words.isEmpty()) return Collections.emptyList();

        List<Long> wordIds = words.stream().map(Word::getId).collect(Collectors.toList());
        List<UserWord> userWords = userWordMapper.selectList(
                new LambdaQueryWrapper<UserWord>()
                        .eq(UserWord::getUserId, userId)
                        .in(UserWord::getWordId, wordIds)
        );
        Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(UserWord::getWordId, Function.identity()));

        return words.stream().map(word -> {
            WordWithStatusVO vo = new WordWithStatusVO();
            vo.setWordId(word.getId());
            vo.setEnglish(word.getEnglish());
            vo.setChinese(word.getChinese());
            UserWord uw = userWordMap.get(word.getId());
            if (uw != null) {
                vo.setWordStatus(uw.getWordStatus());
                vo.setForgetCount(uw.getForgetCount());
            } else {
                vo.setWordStatus(0);
                vo.setForgetCount(0);
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
