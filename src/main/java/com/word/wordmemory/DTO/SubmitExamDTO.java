package com.word.wordmemory.DTO;

import lombok.Data;

/**
 * 提交试卷请求参数
 */
@Data
public class SubmitExamDTO {
    private Long bookId;    // 单词书 ID
    private Integer score;  // 最终得分
}
