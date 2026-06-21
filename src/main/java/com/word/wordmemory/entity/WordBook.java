package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 单词书实体 —— 对应数据库 word_book 表
 * 表名 word_book 由 MyBatis-Plus 驼峰 WordBook 自动转为下划线 word_book，一致故无需 @TableName
 */
@Data
public class WordBook {
    @TableId(type = IdType.AUTO)
    private Long id;              // 单词书 ID
    private String bookName;      // 书名（如 "四级词汇"）
    private String intro;         // 简介
    private String targetUser;    // 目标用户群体（如 "四级"、"六级"、"考研"）
    private LocalDateTime createTime;  // 创建时间
}
