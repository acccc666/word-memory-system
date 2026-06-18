package com.word.wordmemory.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.service.UserWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord> implements UserWordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWordStatus(Long userId, Long wordId, Integer wordStatus) {
        // 1. 查询是否已存在记录
        UserWord userWord = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getWordId, wordId)
                .one();

        LocalDateTime now = LocalDateTime.now(); // 获取当前操作时间

        if (userWord != null) {
            // 2. 之前背过，更新状态和时间
            userWord.setWordStatus(wordStatus);
            userWord.setLastReviewTime(now); // 🌟 极度关键：更新最近一次复习时间！

            if (wordStatus == 0) {
                userWord.setForgetCount(userWord.getForgetCount() + 1);
            }
            updateById(userWord);
        } else {
            // 3. 第一次背，新建记录
            UserWord newUw = new UserWord();
            newUw.setUserId(userId);
            newUw.setWordId(wordId);
            newUw.setWordStatus(wordStatus);
            newUw.setLastReviewTime(now);    // 🌟 极度关键：记录首次背诵时间！
            newUw.setForgetCount(wordStatus == 0 ? 1 : 0);
            save(newUw);
        }
    }
}