package com.word.wordmemory.DTO;

/**
 * 更新单词状态请求参数
 */
public class UpdateWordStatusDTO {
    private Integer wordStatus;  // 0=未记住  1=模糊  2=已记住

    public Integer getWordStatus() { return wordStatus; }
    public void setWordStatus(Integer wordStatus) { this.wordStatus = wordStatus; }
}
