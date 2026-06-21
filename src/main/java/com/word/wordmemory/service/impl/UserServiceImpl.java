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

/**
 * 用户 Service 实现 —— 注册 / 登录的业务逻辑
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 登录
     * 根据用户名查询用户，再用 BCrypt 验证密码。
     * 登录失败统一返回"用户名或密码错误"，不区分是用户不存在还是密码错误。
     */
    @Override
    public User login(String username, String password) {
        // 根据用户名查数据库（lambdaQuery 是 MyBatis-Plus 继承来的）
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();              // 取一条，如果多条会抛异常

        if (user == null) {
            // 不提示"用户不存在"，防止攻击者猜测已注册的用户名
            throw new BusinessException(ResultCode.LOGIN_ERROR.getCode(),
                                        ResultCode.LOGIN_ERROR.getMsg());
        }

        // BCrypt 校验密码：用户输入的明文 vs 数据库中存��密文
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR.getCode(),
                                        ResultCode.LOGIN_ERROR.getMsg());
        }

        return user;  // 返回 User 对象（Controller 取 userId 生成 Token）
    }

    /**
     * 注册
     * 三步：查重 -> BCrypt 加密 -> 写入 MySQL
     *
     * @Transactional 保证三个操作的原子性：
     *   如果 save() 失败，前面的查重操作也回滚
     *   rollbackFor = Exception.class 表示所有异常都回滚（Spring 默认只回滚 RuntimeException）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password) {
        // 第 1 步：查重（SELECT COUNT(*) FROM user WHERE username = ?）
        long count = lambdaQuery().eq(User::getUsername, username).count();
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS.getCode(), "用户名已存在");
        }

        // 第 2 步：BCrypt 加密 —— 存入数据库的是密文，不是明文
        String encryptedPassword = PasswordUtil.encrypt(password);

        // 第 3 步：写入 MySQL
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);   // 密文
        user.setCreateTime(LocalDateTime.now());
        save(user);  // INSERT INTO user(username, password, create_time) VALUES (?, ?, ?)
    }
}
