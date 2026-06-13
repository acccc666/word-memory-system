package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

public class ExamRecord {
    @TableId (type = IdType.AUTO )
    private Long id;
    private Long userId;
    private Long bookId;
    private Integer examNum;
    private Integer score;
    private Integer setTime;
    private Integer examStatus;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
}
