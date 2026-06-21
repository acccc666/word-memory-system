package com.word.wordmemory.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具
 *
 * 使用 BCrypt 自适应哈希算法，特点：
 *   ① 不可逆：无法从密文反推明文
 *   ② 随机盐值：即使相同密码，每次加密结果不同，抵御彩虹表攻击
 *   ③ 自适应：可增加迭代次数对抗硬件加速暴力破解
 *
 * 不需要 @Component：全部是静态方法，直接 PasswordUtil.encrypt() 调用
 */
public class PasswordUtil {

    // BCrypt 加密器，单例（final 保证不变）
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /** 加密密码：明文 -> BCrypt密文（每次结果不同） */
    public static String encrypt(String password) {
        return ENCODER.encode(password);
    }

    /** 验证密码：明文 + 数据库密文 -> 是否匹配 */
    public static boolean verify(String password, String encrypted) {
        return ENCODER.matches(password, encrypted);
    }
}
