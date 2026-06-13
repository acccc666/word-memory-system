package com.word.wordmemory.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class Word {
    @TableId (type= IdType.AUTO)
    private Long id;
    private Long bookId;
    private String english;
    private String chinese;
}

