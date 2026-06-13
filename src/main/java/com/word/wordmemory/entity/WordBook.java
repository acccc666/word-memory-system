package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class WordBook {
    @TableId (type = IdType.AUTO )
    private Long id;
    private String bookName;
    private String intro;
    private String targetUser;
    private LocalDateTime createTime;
}
