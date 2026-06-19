package com.word.wordmemory.entity.vo;

public class WordWithStatusVO {
    private Long wordId;
    private String english;
    private String chinese;
    private Integer wordStatus;
    private Integer forgetCount;

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }
    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }
    public String getChinese() { return chinese; }
    public void setChinese(String chinese) { this.chinese = chinese; }
    public Integer getWordStatus() { return wordStatus; }
    public void setWordStatus(Integer wordStatus) { this.wordStatus = wordStatus; }
    public Integer getForgetCount() { return forgetCount; }
    public void setForgetCount(Integer forgetCount) { this.forgetCount = forgetCount; }
}
