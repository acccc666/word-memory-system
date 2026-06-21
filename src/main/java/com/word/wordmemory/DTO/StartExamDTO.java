package com.word.wordmemory.DTO;

import lombok.Data;

/**
 * 开始考试请求参数
 */
@Data
public class StartExamDTO {
    private Long bookId;                // 要测试的单词书 ID
    private Integer examCount = 20;     // 试题数量（默认 20）
    private Double enToZhRatio = 0.5;   // 英译中题目比例：0.0~1.0（默认 50%）
}
