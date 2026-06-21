package com.word.wordmemory.entity.vo;

/**
 * 开始考试请求（旧版，当前使用 StartExamDTO）
 */
public class ExamStartRequest {
    private Long bookId;
    private String examType;  // 考试题型

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
}
