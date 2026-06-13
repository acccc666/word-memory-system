package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class UserWord {
    @TableId (type = IdType.AUTO )
    private Long id;
    private Long userId;
    private Long wordId;
    private Integer forgetCount;
    private Integer wordStatus;
}

