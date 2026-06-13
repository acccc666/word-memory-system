package com.word.wordmemory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

public class Word {
    @TableId (type= IdType.AUTO)
    private Long id;
    private Long bookId;
    private String english;
    private String chinese;
}
