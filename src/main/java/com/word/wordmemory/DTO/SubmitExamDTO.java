package com.word.wordmemory.DTO;

import lombok.Data;

@Data
public class SubmitExamDTO {
    /**
     * 刚才考的那本单词书 ID
     */
    private Long bookId;

    /**
     * 前端计算出来的最终得分
     */
    private Integer score;
}