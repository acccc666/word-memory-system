package com.word.wordmemory.entity.vo;

import java.util.List;

public class StudySubmitRequest {
    private Long bookId;
    private List<WordResult> results;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public List<WordResult> getResults() { return results; }
    public void setResults(List<WordResult> results) { this.results = results; }

    public static class WordResult {
        private Long wordId;
        private String action; // "remembered" | "fuzzy" | "forgot" | "wrong"

        public Long getWordId() { return wordId; }
        public void setWordId(Long wordId) { this.wordId = wordId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }
}