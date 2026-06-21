package com.word.wordmemory.config;

import com.word.wordmemory.common.result.Result;
import com.word.wordmemory.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录拦截器 —— 校验请求头中的 Authorization Token
 *
 * 实现 HandlerInterceptor.preHandle()，在 Controller 方法执行前调用。
 * 返回 true 放行，返回 false 拦截并返回 401。
 *
 * 校验流程：
 *   ① 放行 OPTIONS 预检请求（浏览器跨域需要）
 *   ② 取 Authorization 请求头
 *   ③ Redis 查询 Token（O(1) 微秒级）
 *   ④ 无效/过期 → 401
 *   ⑤ 有效 → 设 userId 到 request 属性，放行
 */
public class LoginInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    // 构造器注入：CorsConfig 中 new LoginInterceptor(redisTemplate) 时传入
    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        // 第 1 步：放行 OPTIONS 预检请求（浏览器跨域时的试探请求，不带 Authorization 头）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 第 2 步：取 Authorization 请求头中的 Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response, "缺少 token");
            return false;
        }

        // 第 3 步：Redis 查询 —— GET token:xxxxxxxxxxxx
        Object userId = redisTemplate.opsForValue().get(token);
        if (userId == null) {
            // Token 不存在或已过期（超过 24h Redis 自动删除）
            writeUnauthorized(response, "token 无效或已过期");
            return false;
        }

        // 第 4 步：设置 userId 到请求属性，供 Controller 的 @RequestAttribute 获取
        // ((Number) userId).longValue()：Jackson 反序列化数字默认是 Integer，统一转 Long
        request.setAttribute("userId", ((Number) userId).longValue());
        return true;
    }

    /** 写 401 错误响应：JSON 格式 */
    private void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(400);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),
                Result.fail(ResultCode.LOGIN_ERROR.getCode(), msg));
    }
}
