package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.word.wordmemory.entity.Word;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

// 注意：这里的 WordWithStatus 是我们在之前那步定义的 DTO 类
import com.word.wordmemory.algorithm.WordWithStatus;

public interface WordMapper extends BaseMapper<Word> {

    /**
     * 联表查询：获取用户在某本单词书下的所有单词及其记忆状态
     */
    @Select("SELECT w.*, s.status, s.last_review_time " +
            "FROM word w " +
            "LEFT JOIN user_word_status s " +
            "  ON w.id = s.word_id AND s.user_id = #{userId} " +
            "WHERE w.book_id = #{bookId}")
    List<WordWithStatus> selectWordsWithStatus(@Param("userId") Long userId, @Param("bookId") Long bookId);
}