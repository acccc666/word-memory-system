package com.word.wordmemory.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.vo.StudySubmitRequest;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.service.UserWordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            int newStatus;
            boolean incForget = false;
            switch (r.getAction()) {
                case "remembered":
                    newStatus = 2;
                    break;
                case "fuzzy":
                    newStatus = 1;
                    break;
                default: // forgot / wrong
                    newStatus = 0;
                    incForget = true;
                    break;
            }

            UserWord uw = existingMap.get(r.getWordId());
            if (uw != null) {
                uw.setWordStatus(newStatus);
                if (incForget) {
                    uw.setForgetCount(uw.getForgetCount() + 1);
                }
                updateById(uw);
            } else {
                UserWord newUw = new UserWord();
                newUw.setUserId(userId);
                newUw.setWordId(r.getWordId());
                newUw.setWordStatus(newStatus);
                newUw.setForgetCount(incForget ? 1 : 0);
                save(newUw);
            }
        }
    }
}