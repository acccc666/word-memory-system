package com.word.wordmemory.entity.vo;

import java.util.List;

/**
 * 学习结果提交请求体
 */
public class StudySubmitRequest {
    private Long bookId;                // 单词书 ID
    private List<WordResult> results;   // 每个单词的学习结果

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public List<WordResult> getResults() { return results; }
    public void setResults(List<WordResult> results) { this.results = results; }

    /**
     * 单个单词的学习结果
     */
    public static class WordResult {
        private Long wordId;     // 单词 ID
        private String action;   // 学习结果： "remembered" / "fuzzy" / "forgot" / "wrong"

        public Long getWordId() { return wordId; }
        public void setWordId(Long wordId) { this.wordId = wordId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }
}
