package com.word.wordmemory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.word.wordmemory.common.exception.BusinessException;
import com.word.wordmemory.common.result.ResultCode;
import com.word.wordmemory.entity.User;
import com.word.wordmemory.mapper.UserMapper;
import com.word.wordmemory.service.UserService;
import com.word.wordmemory.util.PasswordUtil;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR.getCode(), ResultCode.LOGIN_ERROR.getMsg());
        }
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR.getCode(), ResultCode.LOGIN_ERROR.getMsg());
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password) {
        long count = lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS.getCode(), "用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encrypt(password));
        user.setCreateTime(LocalDateTime.now());
        save(user);
    }
}

