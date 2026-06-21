package com.word.wordmemory.entity.vo;

/**
 * 单词视图对象 —— 前端展示单词时使用的数据结构
 *
 * 合并了 word 表的（english, chinese）和 user_word 表的（wordStatus, forgetCount），
 * 方便前端直接展示，无需再调另一个接口获取记忆状态。
 */
public class WordWithStatusVO {
    private Long wordId;         // 单词 ID
    private String english;      // 英文
    private String chinese;      // 中文释义
    private Integer wordStatus;  // 记忆状态：0=未记住  1=模糊  2=已记住
    private Integer forgetCount; // 遗忘次数
    // Getter / Setter
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
