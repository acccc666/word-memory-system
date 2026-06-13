package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import org.springframework.stereotype.Service;
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
    public List<Word> generateDailyWordList(Long userId, Long bookId, int needCount) {
        // 1. 从数据库中拉取该用户在这本书里【非已记住】状态的所有单词和状态信息
        // TODO: 替换为你的真实 SQL 查询，例如：SELECT w.*, s.status FROM word w JOIN user_word_status s ...
        List<WordWithStatus> rawList = getMockDataFromDb();

        // 2. 构建抽题池 (List)
        List<Word> drawPool = new ArrayList<>();

        for (WordWithStatus item : rawList) {
            int status = item.getStatus(); // 假设 0:未记/未出现, 1:模糊, 2:已记

            if (status == 2) {
                continue; // 已记住的，直接跳过，不放入池子
            } else if (status == 1) {
                drawPool.add(item.getWord()); // 模糊的，放入 1 次
            } else if (status == 0) {
                drawPool.add(item.getWord()); // 未记住的，放入 2 次（概率翻倍，出现两次）
                drawPool.add(item.getWord());
            }
        }

        // 3. 核心机制：彻底打乱池子（洗牌）
        Collections.shuffle(drawPool);

        // 4. 截取所需的数量返回，注意防止越界
        int toIndex = Math.min(needCount, drawPool.size());
        return drawPool.subList(0, toIndex);
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
