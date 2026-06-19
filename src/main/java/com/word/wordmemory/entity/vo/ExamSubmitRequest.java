package com.word.wordmemory.entity.vo;

import java.util.List;

public class ExamSubmitRequest {
    private List<Answer> answers;

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }

    public static class Answer {
        private Long wordId;
        private String selectedAnswer;

        public Long getWordId() { return wordId; }
        public void setWordId(Long wordId) { this.wordId = wordId; }
        public String getSelectedAnswer() { return selectedAnswer; }
        public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    }
}