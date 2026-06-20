package com.word.wordmemory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.entity.vo.StudySubmitRequest;
import java.util.List;

public interface UserWordService extends IService<UserWord> {
    void updateWordStatus(Long userId, Long wordId, Integer wordStatus);
    void batchUpdateStudyResults(Long userId, List<StudySubmitRequest.WordResult> results);
}