package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RandomWordService {

    /**
     * 核心全能出题器：支持英译中、中译英，并智能打乱干扰项
     * 完美适配最新版的 QuizQuestionData
     */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allMeanings, List<String> allEnglish, boolean isEnToZh) {
        QuizQuestionData quiz = new QuizQuestionData();


        quiz.setWordId(Math.toIntExact(correctWord.getId()));

        Set<String> optionSet = new HashSet<>();
        Random random = new Random();
        int maxAttempts = 100; // 防死循环熔断机制
        int attempts = 0;

        if (isEnToZh) {
            // 【题型 1：英译中】
            quiz.setQuestionType(1);
            quiz.setQuestionText(correctWord.getEnglish()); // 题目大字显示英文
            String correctAnswer = correctWord.getChinese();
            quiz.setCorrectAnswer(correctAnswer);

            optionSet.add(correctAnswer); // 必带正确答案

            // 去全书中文库里抓取 3 个干扰项
            while (optionSet.size() < 4 && attempts < maxAttempts) {
                if (allMeanings != null && !allMeanings.isEmpty()) {
                    optionSet.add(allMeanings.get(random.nextInt(allMeanings.size())));
                }
                attempts++;
            }
        } else {
            // 【题型 2：中译英】
            quiz.setQuestionType(2);
            quiz.setQuestionText(correctWord.getChinese()); // 题目大字显示中文
            String correctAnswer = correctWord.getEnglish();
            quiz.setCorrectAnswer(correctAnswer);

            optionSet.add(correctAnswer); // 必带正确答案

            // 去全书英文库里抓取 3 个干扰项
            while (optionSet.size() < 4 && attempts < maxAttempts) {
                if (allEnglish != null && !allEnglish.isEmpty()) {
                    optionSet.add(allEnglish.get(random.nextInt(allEnglish.size())));
                }
                attempts++;
            }
        }

        // 最终步骤：把去重后的 4 个选项转成 List，并进行“洗牌”打乱顺序
        List<String> finalOptions = new ArrayList<>(optionSet);
        Collections.shuffle(finalOptions);
        quiz.setOptions(finalOptions);

        return quiz;
    }
}