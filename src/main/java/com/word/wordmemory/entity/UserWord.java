package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 用户单词状态实体 —— 对应数据库 user_word 表
 *
 * 记录每个用户对每个单词的记忆情况，这是"间隔重复"算法的核心数据。
 * 一个 userId + wordId 对应的记录是唯一的。
 */
@Data
public class UserWord {
    @TableId(type = IdType.AUTO)
    private Long id;              // 自增主键
    private Long userId;          // 用户 ID，已加索引
    private Long wordId;          // 单词 ID
    private Integer forgetCount;  // 遗忘次数（每次标记为忘记就 +1）
    private Integer wordStatus;   // 记忆状态：0=未记住  1=模糊  2=已记住
}
