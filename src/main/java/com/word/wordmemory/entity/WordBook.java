package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

@Data
public class WordBook {
    @TableId (type = IdType.AUTO )
    private Long id;
    private String bookName;
    private String intro;
    private String targetUser;
    private LocalDateTime createTime;
}

