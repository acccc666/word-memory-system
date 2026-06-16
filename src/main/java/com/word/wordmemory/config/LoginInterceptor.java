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
 * 登录拦截器——校验请求头中的 token
 */
public class LoginInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    public LoginInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response, "缺少 token");
            return false;
        }

        // 校验 token 是否在 Redis 中
        Object userId = redisTemplate.opsForValue().get(token);
        if (userId == null) {
            writeUnauthorized(response, "token 无效或已过期");
            return false;
        }

        // 将 userId 存入 request 属性，方便后续使用
        request.setAttribute("userId", ((Number) userId).longValue());
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(),
                Result.fail(ResultCode.LOGIN_ERROR.getCode(), msg));
    }
}




