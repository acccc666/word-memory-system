package com.word.wordmemory.controller;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.entity.User;
import com.word.wordmemory.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.fail(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail(400, "密码不能为空");
        }

        User user = userService.login(username, password);
        String token = "token:" + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(token, user.getId(), 24, TimeUnit.HOURS);
        return Result.success(token);
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty()) {
            return Result.fail(400, "用户名不能为空");
        }
        if (username.trim().length() < 2 || username.trim().length() > 20) {
            return Result.fail(400, "用户名长度应在 2-20 个字符之间");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail(400, "密码不能为空");
        }
        if (password.length() < 6) {
            return Result.fail(400, "密码长度不能少于 6 位");
        }

        userService.register(username, password);
        return Result.success();
    }
}