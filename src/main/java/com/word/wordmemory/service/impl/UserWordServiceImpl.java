package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.vo.StudySubmitRequest;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.service.UserWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord> implements UserWordService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWordStatus(Long userId, Long wordId, Integer wordStatus) {
        UserWord userWord = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getWordId, wordId)
                .one();

        if (userWord != null) {
            userWord.setWordStatus(wordStatus);
            if (wordStatus == 0) {
                userWord.setForgetCount(userWord.getForgetCount() + 1);
            }
            updateById(userWord);
        } else {
            UserWord newUw = new UserWord();
            newUw.setUserId(userId);
            newUw.setWordId(wordId);
            newUw.setWordStatus(wordStatus);
            newUw.setForgetCount(wordStatus == 0 ? 1 : 0);
            save(newUw);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStudyResults(Long userId, List<StudySubmitRequest.WordResult> results) {
        // 1. 查出用户已有的所有 user_word 记录
        List<Long> wordIds = results.stream()
                .map(StudySubmitRequest.WordResult::getWordId)
                .collect(Collectors.toList());
        List<UserWord> existing = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .in(UserWord::getWordId, wordIds)
                .list();
        Map<Long, UserWord> existingMap = existing.stream()
                .collect(Collectors.toMap(UserWord::getWordId, uw -> uw));

        // 2. 逐条处理
        for (StudySubmitRequest.WordResult r : results) {
            boolean isRemembered = "remembered".equals(r.getAction());
            int newStatus = isRemembered ? 2 : 0;

            UserWord uw = existingMap.get(r.getWordId());
            if (uw != null) {
                uw.setWordStatus(newStatus);
                // 只要不是"已记住"，遗忘次数 +1
                if (!isRemembered) {
                    uw.setForgetCount(uw.getForgetCount() + 1);
                }
                updateById(uw);
            } else {
                UserWord newUw = new UserWord();
                newUw.setUserId(userId);
                newUw.setWordId(r.getWordId());
                newUw.setWordStatus(newStatus);
                newUw.setForgetCount(isRemembered ? 0 : 1);
                save(newUw);
            }
        }
    }
}