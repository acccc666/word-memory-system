package com.word.wordmemory.algorithm;

import lombok.Data;
import java.util.List;

@Data
public class QuizQuestionData {
    private Long wordId;          // 单词ID
    private String englishWord;   // 英文单词
    private List<String> options; // 打乱后的4个中文选项
    private String correctAnswer; // 正确的中文释义（用于前端或后端校验）
}
