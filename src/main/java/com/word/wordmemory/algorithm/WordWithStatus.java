package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordWithStatus {
    private Word word;
    private Integer status; // 0:未记, 1:模糊, 2:已记

    // 新增：记录该用户上一次背这个词的时间
    private LocalDateTime lastReviewTime;

    // 新增：不存数据库，只在算法执行时作为排序依据
    private Double priorityScore;
}