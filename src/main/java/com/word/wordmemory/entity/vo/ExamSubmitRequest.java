package com.word.wordmemory.entity.vo;

/**
 * 提交考试请求（旧版，当前使用 SubmitExamDTO）
 */
public class ExamSubmitRequest {
    private Long examRecordId;
    private Integer score;

    public Long getExamRecordId() { return examRecordId; }
    public void setExamRecordId(Long examRecordId) { this.examRecordId = examRecordId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    /**
     * 答题记录 —— 每个单词的选择结果
     */
    public static class Answer {
        private Long wordId;       // 单词 ID
        private String selected;   // 用户选择的选项
        private boolean correct;   // 是否正确

        public Long getWordId() { return wordId; }
        public void setWordId(Long wordId) { this.wordId = wordId; }
        public String getSelected() { return selected; }
        public void setSelected(String selected) { this.selected = selected; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
    }
}
