package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.word.wordmemory.mapper.WordMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RandomWordService {

    // 假设你有一个用于操作数据库的 Mapper，这里先用伪代码表示
    // @Autowired
    // private WordMapper wordMapper;

    // @Autowired
    // private UserWordStatusMapper statusMapper;

    /**
     * 核心算法 1：根据记忆状态动态生成背单词列表（内存池化 + 洗牌算法）
     * * @param userId 用户的 ID
     * @param bookId 单词书的 ID
     * @param needCount 前端请求的单词数量（如 20 个）
     * @return 动态按频次生成的单词集合
     */
    @Autowired
    private WordMapper wordMapper;

    public List<Word> generateDailyWordList(Long userId, Long bookId, int needCount) {

        // ?? 2. 替换为真实的数据库查询！
        List<WordWithStatus> rawList = wordMapper.selectWordsWithStatus(userId, bookId);

        LocalDateTime now = LocalDateTime.now();

        // 3. 后面的核心计算流水线保持不变...
        List<Word> finalWordList = rawList.stream()
                .filter(item -> {
                    // 防御性编程：如果是第一次背，数据库里没状态记录（status 为 null），当作未记(0)处理
                    if (item.getStatus() == null) {
                        item.setStatus(0);
                    }
                    return item.getStatus() != 2; // 踢掉已记住的
                })
                .peek(item -> {
                    long hoursPassed = 0;
                    // 如果是没背过的新词，lastReviewTime 也是 null
                    if (item.getLastReviewTime() != null) {
                        hoursPassed = Duration.between(item.getLastReviewTime(), now).toHours();
                    } else {
                        hoursPassed = 72; // 新词默认给较高曝光率
                    }

                    double baseScore = (item.getStatus() == 0) ? 100.0 : 60.0;
                    double timeDecayBonus = hoursPassed * 1.5;
                    item.setPriorityScore(baseScore + timeDecayBonus);
                })
                .sorted((a, b) -> Double.compare(b.getPriorityScore(), a.getPriorityScore()))
                .limit(needCount)
                .map(WordWithStatus::getWord)
                .collect(Collectors.toList());

        return finalWordList;
    }

    /**
     * 核心算法 2：生成单道自测题，配上 1对 3错 的随机干扰选项
     * * @param correctWord 正确的单词对象
     * @param allBookMeanings 该单词书中所有单词的中文释义集合（用于抽干扰项）
     * @return 封装好的测试题 DTO
     */
    public QuizQuestionData generateSingleQuiz(Word correctWord, List<String> allBookMeanings) {
        QuizQuestionData quiz = new QuizQuestionData();
        quiz.setWordId(correctWord.getId());
        quiz.setEnglishWord(correctWord.getEnglish());

        String correctAnswer = correctWord.getChinese();
        quiz.setCorrectAnswer(correctAnswer);

        // 1. 使用 Set 来保证选项的唯一性（防止抽到重复的错误答案）
        Set<String> optionSet = new HashSet<>();
        optionSet.add(correctAnswer); // 先把正确答案放进去

        // 2. 随机抽取干扰项
        Random random = new Random();
        int maxAttempts = 100; // 防止死循环的安全阀
        int attempts = 0;

        // 循环直到凑满 4 个选项
        while (optionSet.size() < 4 && attempts < maxAttempts) {
            // 随机生成一个索引，取出对应的中文释义
            int randomIndex = random.nextInt(allBookMeanings.size());
            String randomMeaning = allBookMeanings.get(randomIndex);

            // Set 会自动去重，如果抽到了和正确答案一样的，或者抽到了重复的干扰项，加不进去
            optionSet.add(randomMeaning);
            attempts++;
        }

        // 3. 将 Set 转为 List 并打乱顺序（让正确答案随机出现在 A/B/C/D 的位置）
        List<String> finalOptions = new ArrayList<>(optionSet);
        Collections.shuffle(finalOptions);

        quiz.setOptions(finalOptions);
        return quiz;
    }
}
