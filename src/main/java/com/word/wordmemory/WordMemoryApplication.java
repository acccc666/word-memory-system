package com.word.wordmemory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 单词组记系统 —— 应用启动入口
 *
 * @MapperScan 扫描 mapper 包下所有 BaseMapper 接口，MyBatis-Plus 自动生成代理实现类
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 */
@MapperScan("com.word.wordmemory.mapper")
@SpringBootApplication
public class WordMemoryApplication {

    public static void main(String[] args) {
        // 启动内嵌 Tomcat，加载所有 Bean，完成自动配置
        SpringApplication.run(WordMemoryApplication.class, args);
    }

}
