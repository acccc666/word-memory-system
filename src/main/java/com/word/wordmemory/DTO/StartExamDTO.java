package com.word.wordmemory.DTO;

import lombok.Data;

@Data
public class StartExamDTO {
    /**
     * 要测试的单词书 ID
     */
    private Long bookId;

    /**
     * 测试数量（默认20）
     */
    private Integer examCount = 20;

    /**
     * 题型比例：看英文选中文的比例（0.0 ~ 1.0）
     * 例如：0.7 表示 70% 的题目是看英文选中文，30% 是看中文选英文
     */
    private Double enToZhRatio = 0.5;
}