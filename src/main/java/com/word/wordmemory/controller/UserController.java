package com.word.wordmemory.controller;

import com.word.wordmemory.common.Result;
import com.word.wordmemory.common.ResultCode;
import com.word.wordmemory.entity.User;
import com.word.wordmemory.service.UserService;
import com.word.wordmemory.util.Md5Util;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/list")
    public Result<List<User>> listUser() {
        return Result.success(userService.list());
    }

    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password) {

        String encryptPwd = Md5Util.encrypt(password);
        User user = userService.lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getPassword, encryptPwd)
                .one();
        if (user == null) {
            return Result.fail(ResultCode.LOGIN_ERROR);
        }
        // 3. 生成token并存入Redis，过期30分钟
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(token, user, 30, TimeUnit.MINUTES);
        return Result.success(token);
    }


    @PostMapping("/register")
    public Result<Void> register(@RequestParam String username,
                                 @RequestParam String password) {
        long count = userService.lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) {
            return Result.fail();
        }
        String encryptPwd = Md5Util.encrypt(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPwd);
        userService.save(user);
        return Result.success();
    }
}