package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomWordService {

    /**
     * 生成单道自测题，配上 1 对 3 错的随机干扰选项
     * @param correctWord 正确的单词对象
     * @param allBookMeanings 该单词书中所有单词的中文释义集合（用于抽干扰项）
     * @return 封装好的测试题 DTO
     */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allBookMeanings) {
        if (allBookMeanings == null || allBookMeanings.isEmpty()) {
            return null;
        }
        QuizQuestionData quiz = new QuizQuestionData();
        quiz.setWordId(correctWord.getId());
        quiz.setEnglishWord(correctWord.getEnglish());

        String correctAnswer = correctWord.getChinese();
        quiz.setCorrectAnswer(correctAnswer);

        // 1. 使用 Set 保证选项唯一性（防止抽到重复干扰项）
        Set<String> optionSet = new HashSet<>();
        optionSet.add(correctAnswer);

        // 2. 随机抽取干扰项，凑满 4 个选项
        Random random = new Random();
        int maxAttempts = 100;
        int attempts = 0;

        while (optionSet.size() < 4 && attempts < maxAttempts) {
            int randomIndex = random.nextInt(allBookMeanings.size());
            optionSet.add(allBookMeanings.get(randomIndex));
            attempts++;
        }

        // 3. 打乱顺序，让正确答案随机出现
        List<String> finalOptions = new ArrayList<>(optionSet);
        Collections.shuffle(finalOptions);
        quiz.setOptions(finalOptions);
        return quiz;
    }
}
