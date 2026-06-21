package com.word.wordmemory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.word.wordmemory.entity.Word;
import com.word.wordmemory.algorithm.WordWithStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 单词 Mapper
 *
 * 除 BaseMapper 的标准 CRUD 外，还自定义了一个 @Select 关联查询方法，
 * 用于直接通过 SQL JOIN 查询单词 + 记忆状态（当前未使用，由 Java 内存关联替代）
 */
@Mapper
public interface WordMapper extends BaseMapper<Word> {

    /** 关联查询：单词 + 用户对该单词的记忆状态（使用 LEFT JOIN） */
    @Select("SELECT w.*, s.word_status AS status " +
            "FROM word w " +
            "LEFT JOIN user_word s ON w.id = s.word_id AND s.user_id = #{userId} " +
            "WHERE w.book_id = #{bookId}")
    List<WordWithStatus> selectWordsWithStatus(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId);
}
