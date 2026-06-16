package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.service.UserWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord> implements UserWordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWordStatus(Long userId, Long wordId, Integer wordStatus) {
        // 先查是否已有记录
        UserWord userWord = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getWordId, wordId)
                .one();

        if (userWord != null) {
            // 更新已有记录
            userWord.setWordStatus(wordStatus);
            if (wordStatus == 0) {
                userWord.setForgetCount(userWord.getForgetCount() + 1);
            }
            updateById(userWord);
        } else {
            // 没有记录则新增
            UserWord newUw = new UserWord();
            newUw.setUserId(userId);
            newUw.setWordId(wordId);
            newUw.setWordStatus(wordStatus);
            newUw.setForgetCount(wordStatus == 0 ? 1 : 0);
            save(newUw);
        }
    }
}