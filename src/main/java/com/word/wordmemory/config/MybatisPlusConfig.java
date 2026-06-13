package com.word.wordmemory.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

public class MybatisPlusConfig {
    public MybatisPlusInterceptor mybatisPlusInterceptor (){
        MybatisPlusInterceptor interceptor =new MybatisPlusInterceptor();
interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL ) );
        return interceptor;
    }
}
