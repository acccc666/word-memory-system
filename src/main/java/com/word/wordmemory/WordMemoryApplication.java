package com.word.wordmemory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.word.wordmemory.mapper")
@SpringBootApplication
public class WordMemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordMemoryApplication.class, args);
    }

}
