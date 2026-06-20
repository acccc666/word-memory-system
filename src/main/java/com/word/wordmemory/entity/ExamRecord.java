package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试历史记录实体类
 */
@Data
@TableName("test_record") // 指定对应的数据库表名
public class ExamRecord {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    private Long userId;

    private Long bookId;

    /**
     * 考试得分
     */
    private Integer score;

    /**
     * 记录创建时间（交卷时间）
     */
    private LocalDateTime createTime;
}