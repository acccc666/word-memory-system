package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 考试记录实体 —— 对应数据库 exam_record 表
 * 记录每次考试的试题数、得分、完成状态
 */
@Data
public class ExamRecord {
    @TableId(type = IdType.AUTO)
    private Long id;              // 记录 ID
    private Long userId;          // 用户 ID
    private Long bookId;          // 单词书 ID
    private Integer examNum;      // 试题数量
    private Integer score;        // 得分
    private Integer setTime;      // 规定用时（分钟）
    private Integer examStatus;   // 考试状态：0=未完成  1=已完成
    private LocalDateTime createTime;  // 创建时间（开始时间）
    private LocalDateTime endTime;     // 完成时间
}
