package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体 —— 对应数据库 user 表
 *
 * @TableName("user") 指定表名（user 是 MySQL 关键字，显式声明更安全）
 * @TableId(type = IdType.AUTO)  主键自增
 * implements Serializable 支持 Redis 序列化
 */
@Data
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;              // 用户 ID（自增）
    private String username;      // 用户名（唯一）
    private String password;      // BCrypt 密文（非明文）
    private LocalDateTime createTime;  // 注册时间
}
