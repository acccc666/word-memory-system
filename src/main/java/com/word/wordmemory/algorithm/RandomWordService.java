package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomWordService {

    /** 英译中出题 */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allBookMeanings) {
        if (allBookMeanings == null || allBookMeanings.isEmpty()) {
            return null;
        }
        QuizQuestionData quiz = new QuizQuestionData();
        quiz.setWordId(correctWord.getId());
        quiz.setEnglishWord(correctWord.getEnglish());
        String correctAnswer = correctWord.getChinese();
        quiz.setCorrectAnswer(correctAnswer);

        Set<String> optionSet = new HashSet<>();
        optionSet.add(correctAnswer);
        Random random = new Random();
        int maxAttempts = 100, attempts = 0;
        while (optionSet.size() < 4 && attempts < maxAttempts) {
            optionSet.add(allBookMeanings.get(random.nextInt(allBookMeanings.size())));
            attempts++;
        }
        List<String> finalOptions = new ArrayList<>(optionSet);
        Collections.shuffle(finalOptions);
        quiz.setOptions(finalOptions);
        return quiz;
    }

    /** 中译英出题 */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allMeanings, List<String> allEnglish, boolean isEnToZh) {
        if (isEnToZh) {
            return generateSingleQuiz(correctWord, allMeanings);
        }
        // 中文→英文：显示中文，选项是英文
        QuizQuestionData quiz = new QuizQuestionData();
        quiz.setWordId(correctWord.getId());
        quiz.setEnglishWord(correctWord.getChinese());
        quiz.setCorrectAnswer(correctWord.getEnglish());

        Set<String> optionSet = new HashSet<>();
        optionSet.add(correctWord.getEnglish());
        Random random = new Random();
        int maxAttempts = 100, attempts = 0;
        while (optionSet.size() < 4 && attempts < maxAttempts) {
            optionSet.add(allEnglish.get(random.nextInt(allEnglish.size())));
            attempts++;
        }
        List<String> finalOptions = new ArrayList<>(optionSet);
        Collections.shuffle(finalOptions);
        quiz.setOptions(finalOptions);
        return quiz;
    }
}