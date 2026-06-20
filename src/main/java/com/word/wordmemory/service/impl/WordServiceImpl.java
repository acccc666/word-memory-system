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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {

    @Autowired
    private UserWordMapper userWordMapper;

    @Override
    public List<WordWithStatusVO> getWordsWithStatus(Long bookId, Long userId) {
        // 分两段查询：先查单词，再查 user_word 记忆状态，内存中拼装
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

    
    @Override
    public IPage<WordWithStatusVO> getWordsWithStatusPage(Long bookId, Long userId, int page, int size) {
        Page<Word> wordPage = new Page<>(page, size);
        Page<Word> result = lambdaQuery().eq(Word::getBookId, bookId).page(wordPage);
        if (result.getRecords().isEmpty()) {
            Page<WordWithStatusVO> empty = new Page<>(page, size);
            empty.setRecords(java.util.Collections.emptyList());
            return empty;
        }
        java.util.List<Long> wordIds = result.getRecords().stream().map(Word::getId).collect(java.util.stream.Collectors.toList());
        java.util.List<UserWord> userWords = userWordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserWord>()
                        .eq(UserWord::getUserId, userId)
                        .in(UserWord::getWordId, wordIds)
        );
        java.util.Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(uw -> uw.getWordId(), java.util.function.Function.identity()));
        java.util.List<WordWithStatusVO> voList = result.getRecords().stream().map(word -> {
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
        }).collect(java.util.stream.Collectors.toList());
        Page<WordWithStatusVO> voPage = new Page<>(page, size);
        voPage.setRecords(voList);
        voPage.setTotal(result.getTotal());
        return voPage;
    }

@Override
    public List<WordWithStatusVO> getStudyWords(Long bookId, Long userId, int needCount) {
        // 1. 获取该单词书下所有单词及用户的记忆状态
        List<WordWithStatusVO> allWords = getWordsWithStatus(bookId, userId);

        // 2. 根据记忆状态构建题目池：
        //    - 已记住(2) → 不出现
        //    - 模糊(1)   → 出现 1 次
        //    - 未记住(0) → 出现 2 次
        List<WordWithStatusVO> pool = new ArrayList<>();
        for (WordWithStatusVO word : allWords) {
            if (word.getWordStatus() == 2) continue; // 已记住，跳过
            pool.add(word);                          // 模糊/未记住都至少出现一次
            if (word.getWordStatus() == 0) {
                pool.add(word);                      // 未记住再追加一次
            }
        }

        // 3. 洗牌打乱顺序
        Collections.shuffle(pool, new Random());

        // 4. 按用户需要的数量截取（如果池子不够就全返回）
        int limit = Math.min(needCount, pool.size());
        return pool.subList(0, limit);
    }
}
