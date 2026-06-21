package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.vo.StudySubmitRequest;
import com.word.wordmemory.mapper.UserWordMapper;
import com.word.wordmemory.service.UserWordService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户单词状态 Service 实现
 *
 * 负责两种修改单词状态的入口：
 *   ① 单个修改（PUT /user-words/{wordId}/status）
 *   ② 批量提交（POST /study/submit 学习结果）
 */
@Service
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord>
        implements UserWordService {

    /**
     * 修改单个单词的记忆状态
     *
     * 有旧记录 → UPDATE
     * 无旧记录 → INSERT（首次背这个单词）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWordStatus(Long userId, Long wordId, Integer wordStatus) {
        // 查 user_word 表，看是否有这个用户对这个单词的历史记录
        UserWord userWord = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .eq(UserWord::getWordId, wordId)
                .one();

        if (userWord != null) {
            // 有旧记录：更新状态
            userWord.setWordStatus(wordStatus);
            if (wordStatus == 0) {  // 标记为"未记住"，遗忘次数 +1
                userWord.setForgetCount(userWord.getForgetCount() + 1);
            }
            updateById(userWord);  // UPDATE user_word SET ... WHERE id = ?
        } else {
            // 无旧记录：插入新记录
            UserWord newUw = new UserWord();
            newUw.setUserId(userId);
            newUw.setWordId(wordId);
            newUw.setWordStatus(wordStatus);
            // 首次就标记为"未记住"，遗忘次数直接为 1
            newUw.setForgetCount(wordStatus == 0 ? 1 : 0);
            save(newUw);  // INSERT INTO user_word ...
        }
    }

    /**
     * 批量提交学习结果（用户学完一组单词后提交）
     *
     * 优化：一次性查出所有已有记录到内存（IN 查询），
     * 避免逐条查数据库的 N+1 问题。
     *
     * action 映射：
     *   "remembered" → 状态 2（已记住）
     *   "fuzzy"      → 状态 1（模糊）
     *   "forgot"/"wrong" → 状态 0（未记住），遗忘次数 +1
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStudyResults(
            Long userId, List<StudySubmitRequest.WordResult> results) {

        // 第 1 步：一次性查出所有要更新的已有记录
        List<Long> wordIds = results.stream()
                .map(StudySubmitRequest.WordResult::getWordId)
                .collect(Collectors.toList());
        List<UserWord> existing = lambdaQuery()
                .eq(UserWord::getUserId, userId)
                .in(UserWord::getWordId, wordIds)
                .list();
        Map<Long, UserWord> existingMap = existing.stream()
                .collect(Collectors.toMap(UserWord::getWordId, uw -> uw));

        // 第 2 步：逐条处理（查 Map 而非数据库）
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
                default:  // "forgot" / "wrong"
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
                try {
                    UserWord newUw = new UserWord();
                    newUw.setUserId(userId);
                    newUw.setWordId(r.getWordId());
                    newUw.setWordStatus(newStatus);
                    newUw.setForgetCount(incForget ? 1 : 0);
                    save(newUw);
                } catch (DuplicateKeyException e) {
                    UserWord existingRecord = lambdaQuery()
                            .eq(UserWord::getUserId, userId)
                            .eq(UserWord::getWordId, r.getWordId()).one();
                    if (existingRecord != null) {
                        existingRecord.setWordStatus(newStatus);
                        if (incForget) {
                            existingRecord.setForgetCount(existingRecord.getForgetCount() + 1);
                        }
                        updateById(existingRecord);
                    }
                }
            }
        }
    }
}
