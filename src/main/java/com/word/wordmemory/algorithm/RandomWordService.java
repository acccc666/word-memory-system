package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import com.word.wordmemory.entity.UserWord;
import com.word.wordmemory.service.WordService;
import com.word.wordmemory.service.UserWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RandomWordService {

    @Autowired
    private WordService wordService;

    @Autowired
    private UserWordService userWordService;

    /**
     * 艾宾浩斯时间衰减算法引擎：生成今日背单词列表
     */
    public List<Word> generateDailyWordList(Long userId, Long bookId, Integer needCount) {
        // 1. 获取全书单词库
        List<Word> allBookWords = wordService.lambdaQuery().eq(Word::getBookId, bookId).list();

        // 2. 获取用户当前书本的背单词记录，并转为 Map 加速查询
        List<UserWord> userWords = userWordService.lambdaQuery().eq(UserWord::getUserId, userId).list();
        Map<Long, UserWord> userWordMap = userWords.stream()
                .collect(Collectors.toMap(UserWord::getWordId, uw -> uw, (v1, v2) -> v2));

        LocalDateTime now = LocalDateTime.now();

        // 3. 核心衰减排序：根据时间衰减算法打分并排序
        return allBookWords.stream()
                .sorted((w1, w2) -> {
                    UserWord uw1 = userWordMap.get(w1.getId());
                    UserWord uw2 = userWordMap.get(w2.getId());

                    double score1 = calculateDecayScore(uw1, now);
                    double score2 = calculateDecayScore(uw2, now);

                    // 倒序排：分数越高的优先级越大，越排在前面
                    return Double.compare(score2, score1);
                })
                .limit(needCount) // 只截取前端需要的数量
                .collect(Collectors.toList());
    }

    /**
     * 计算单词的衰减权重分
     */
    private double calculateDecayScore(UserWord uw, LocalDateTime now) {
        // 如果从来没背过，赋予极高分，优先出题
        if (uw == null || uw.getLastReviewTime() == null) {
            return 10000.0;
        }
        // 计算距离上次复习经过了多少个小时
        long hoursSinceLast = Math.max(0, ChronoUnit.HOURS.between(uw.getLastReviewTime(), now));
        // 获取错误次数
        int forgetCount = uw.getForgetCount() != null ? uw.getForgetCount() : 0;

        // 核心公式: 基础分(错误次数*10) + 时间衰减加成(闲置小时数*1.5)
        return (forgetCount * 10) + (hoursSinceLast * 1.5);
    }

    /**
     * 核心全能出题器：支持英译中、中译英，并智能打乱干扰项
     * 完美适配最新版的 QuizQuestionData
     */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allMeanings, List<String> allEnglish, boolean isEnToZh) {
        QuizQuestionData quiz = new QuizQuestionData();

        // 🌟 小贴士：如果你的 QuizQuestionData 里的 wordId 是 Integer 类型，
        // 而 Word 实体的 id 是 Long 类型，这里可能会报错。
        // 如果报错了，要么把 QuizQuestionData 的 wordId 改成 Long，要么在这里加个 .intValue()
        // quiz.setWordId(correctWord.getId().intValue());
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