package com.word.wordmemory.entity.vo;

public class ExamStartRequest {
    private Long bookId;
    private Integer questionCount;
    private Integer setTime;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
    public Integer getSetTime() { return setTime; }
    public void setSetTime(Integer setTime) { this.setTime = setTime; }
}