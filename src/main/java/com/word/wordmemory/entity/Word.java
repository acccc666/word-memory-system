package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 单词实体 —— 对应数据库 word 表
 * 每个单词属于某本单词书（book_id 关联 word_book 表）
 */
@Data
public class Word {
    @TableId(type = IdType.AUTO)
    private Long id;          // 单词 ID
    private Long bookId;      // 所属单词书的 ID，外键，已加索引
    private String english;   // 英文单词
    private String chinese;   // 中文释义
}
