package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class UserWord {
    @TableId (type = IdType.AUTO )
    private Long id;
    private Long userId;
    private Long wordId;
    private Integer forgetCount;
    private Integer wordStatus;
}
