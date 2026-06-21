package com.word.wordmemory.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置 —— 注册分页插件
 *
 * 工作原理：拦截所有参数带 Page 对象的查询方法，自动拼接 SQL：
 *   ① 先执行 SELECT COUNT(*) 算总条数
 *   ② 再执行 SELECT ... LIMIT ?, ? 取当前页数据
 *   ③ 封装到 Page 对象的 records 和 total 中
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // DbType.MYSQL 指定 MySQL 的 LIMIT 语法
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
