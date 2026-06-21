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

/**
 * 用户控制器 —— 注册 / 登录
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录接口
     * POST /user/login
     * Body: { "username": "xxx", "password": "xxx" }
     * Response: { "code": 200, "data": "token:xxxxxxxxxxxx" }
     *
     * 流程：
     *   ① 参数校验（非空）
     *   ② Service 验证用户名密码（失败抛 BusinessException）
     *   ③ 生成 UUID Token（"token:" + UUID）
     *   ④ 存入 Redis（key=token, value=userId, TTL=24h）
     *   ⑤ 返回 Token 给前端
     */
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

        // Service 层验证：查用户 + BCrypt 校验密码
        User user = userService.login(username, password);

        // 生成 UUID Token（去横杠，32 位十六进制）
        String token = "token:" + UUID.randomUUID().toString().replace("-", "");

        // 存入 Redis，24 小时自动过期
        redisTemplate.opsForValue().set(token, user.getId(), 24, TimeUnit.HOURS);

        return Result.success(token);
    }

    /**
     * 注册接口
     * POST /user/register
     * Body: { "username": "xxx", "password": "xxx" }
     * Response: { "code": 200, "msg": "操作成功" }
     *
     * 流程：
     *   ① 参数校验（非空、长度范围）
     *   ② Service 注册（查重 -> BCrypt 加密 -> 写入 MySQL）
     *   ③ 返回成功
     */
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
