package com.word.wordmemory.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 使用 BCrypt 自适应哈希算法，每次加密生成不同结果，可抵御暴力破解。
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码（每次结果不同，自带随机盐值）
     */
    public static String encrypt(String password) {
        return ENCODER.encode(password);
    }

    /**
     * 验证密码
     */
    public static boolean verify(String password, String encrypted) {
        return ENCODER.matches(password, encrypted);
    }
}
