package com.word.wordmemory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.word.wordmemory.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
    void register(String username, String password);
}
