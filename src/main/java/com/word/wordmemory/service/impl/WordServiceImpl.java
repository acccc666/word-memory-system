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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 单词 Service 实现
 *
 * 核心查询逻辑：分步查询 + 内存关联
 *   先查 word 表拿到单词，再查 user_word 表拿到状态，
 *   在 Java 中用 Map 按 wordId 关联拼装，避免复杂的 SQL JOIN。
 */
@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word> implements WordService {

    @Autowired
    private UserWordMapper userWordMapper;

    /**
     * 查询某本书的所有单词（带当前用户的记忆状态）
     *
     * 两步查询 + 内存关联：
     *   ① 查 word 表：SELECT * FROM word WHERE book_id = ?
     *   ② 查 user_word 表：SELECT * FROM user_word WHERE user_id = ? AND word_id IN (...)
     *   ③ 在 Java 中用 Map<wordId, UserWord> 关联拼装
     */
    @Override
    public List<WordWithStatusVO> getWordsWithStatus(Long bookId, Long userId) {
        // 第 1 步：查这本书的所有单词
        List<Word> words = lambdaQuery().eq(Word::getBookId, bookId).list();
        if (words.isEmpty()) return Collections.emptyList();

        // 提取单词 ID 列表，为下一步 IN 查询做准备
        List<Long> wordIds = words.stream().map(Word::getId).collect(Collectors.toList());

        // 第 2 步：查当前用户对这些单词的记忆状态（只查这本书涉及的单词）
        List<UserWord> userWords = userWordMapper.selectList(
                new LambdaQueryWrapper<UserWord>()
                        .eq(UserWord::getUserId, userId)
                        .in(UserWord::getWordId, wordIds)
        );

        // 第 3 步：转为 Map<wordId, UserWord>，O(1) 快速查找
        Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(UserWord::getWordId, Function.identity()));

        // 第 4 步：拼装 VO —— 每个单词去 Map 中取记忆状态
        return words.stream().map(word -> {
            WordWithStatusVO vo = new WordWithStatusVO();
            vo.setWordId(word.getId());
            vo.setEnglish(word.getEnglish());
            vo.setChinese(word.getChinese());

            UserWord uw = userWordMap.get(word.getId());
            if (uw != null) {
                // 有记录：取数据库中的状态
                vo.setWordStatus(uw.getWordStatus());
                vo.setForgetCount(uw.getForgetCount());
            } else {
                // 没记录：说明从未背过，默认未记住(0)、遗忘0次
                vo.setWordStatus(0);
                vo.setForgetCount(0);
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 分页查询单词（带记忆状态）
     *
     * 和 getWordsWithStatus 的区别：这里只查当前页的单词和状态，
     * 而非整本书，所以数据量更小，分页效率高。
     */
    @Override
    public IPage<WordWithStatusVO> getWordsWithStatusPage(
            Long bookId, Long userId, int page, int size) {

        Page<Word> wordPage = new Page<>(page, size);
        Page<Word> result = lambdaQuery()
                .eq(Word::getBookId, bookId).page(wordPage);

        if (result.getRecords().isEmpty()) {
            Page<WordWithStatusVO> empty = new Page<>(page, size);
            empty.setRecords(Collections.emptyList());
            return empty;
        }

        // 只查当前页单词的记忆状态（IN 查询，不是全量）
        List<Long> wordIds = result.getRecords().stream()
                .map(Word::getId).collect(Collectors.toList());
        List<UserWord> userWords = userWordMapper.selectList(
                new LambdaQueryWrapper<UserWord>()
                        .eq(UserWord::getUserId, userId)
                        .in(UserWord::getWordId, wordIds)
        );
        Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(uw -> uw.getWordId(), Function.identity()));

        // 拼装 VO
        List<WordWithStatusVO> voList = result.getRecords().stream().map(word -> {
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

        Page<WordWithStatusVO> voPage = new Page<>(page, size);
        voPage.setRecords(voList);
        voPage.setTotal(result.getTotal());  // 总数取自 word 表 COUNT
        return voPage;
    }

    /**
     * 获取学习单词列表 —— 按记忆状态的调度算法
     *
     * 调度规则：
     *   已记住(2) → 不出现
     *   模糊(1)   → 出现 1 次
     *   未记住(0) → 出现 2 次（强化记忆）
     *
     * 符合间隔重复（Spaced Repetition）的简化实现：
     *   记得越差的词出现频率越高。
     */
    @Override
    public List<WordWithStatusVO> getStudyWords(Long bookId, Long userId, int needCount) {
        // 获取全量单词及状态
        List<WordWithStatusVO> allWords = getWordsWithStatus(bookId, userId);

        // 按状态构建学习池：记住(2)跳过，模糊(1)放1次，未记住(0)放2次
        List<WordWithStatusVO> pool = new ArrayList<>();
        for (WordWithStatusVO word : allWords) {
            if (word.getWordStatus() == 2) continue;  // 已记住，跳过
            pool.add(word);                              // 模糊/未记住至少出现一次
            if (word.getWordStatus() == 0) {
                pool.add(word);                          // 未记住再追加一次
            }
        }

        // 洗牌打乱顺序（避免顺序效应）
        Collections.shuffle(pool, new Random());

        // 按需截取（池子不够就全返回）
        int limit = Math.min(needCount, pool.size());
        List<WordWithStatusVO> resultList = new ArrayList<>(pool.subList(0, limit));

        // 滑动窗口防重叠：防止同一个单词挨在一起
        for (int i = 0; i < resultList.size() - 1; i++) {
            if (resultList.get(i).getWordId().equals(resultList.get(i + 1).getWordId())) {
                for (int j = i + 2; j < resultList.size(); j++) {
                    if (!resultList.get(j).getWordId().equals(resultList.get(i).getWordId())) {
                        Collections.swap(resultList, i + 1, j);
                        break;
                    }
                }
            }
        }

        return resultList;
    }
}
