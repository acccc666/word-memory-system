package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

@Data
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

