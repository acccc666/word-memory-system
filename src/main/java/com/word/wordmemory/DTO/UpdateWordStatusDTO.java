package com.word.wordmemory.DTO;
import lombok.Data;

/**
 * 接收前端背单词时，点击“记得/模糊/不记得”传来的状态更新数据
 */
@Data
public class UpdateWordStatusDTO {

    // 单词书ID（可选，方便统计某本书的进度）
    private Long bookId;

    // 刚刚背诵的单词ID
    private Long wordId;

    /**
     * 用户的记忆状态：
     * 0: 不记得
     * 1: 模糊
     * 2: 记得
     */
    private Integer status;
}