package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.word.wordmemory.entity.User;

/**
 * 用户 Mapper —— 继承 BaseMapper，MyBatis-Plus 自动提供 CRUD 方法
 * 无需手写 INSERT/UPDATE/SELECT SQL
 */
public interface UserMapper extends BaseMapper<User> {
}
