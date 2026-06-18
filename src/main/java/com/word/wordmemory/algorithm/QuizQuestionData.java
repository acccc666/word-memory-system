package com.word.wordmemory.algorithm;

import lombok.Data;
import java.util.List;


@Data // 使用 Lombok 自动生成 get/set 方法，不用手写了！
public class QuizQuestionData {

    /**
     * 单词的唯一 ID，方便后续提交试卷时统计对错
     */
    private Integer wordId; // 如果你数据库是 Long 类型，这里就改成 Long

    /**
     * 【新增】题目类型
     * 1: 英译中 (看英文选中文)
     * 2: 中译英 (看中文选英文)
     */
    private Integer questionType;

    /**
     * 【新增】题干文本
     * 如果题型是 1，这里存的就是英文单词 (如 "apple")
     * 如果题型是 2，这里存的就是中文释义 (如 "苹果")
     * 前端不需要写判断逻辑，直接把这个字段显示在题目的大标题上即可。
     */
    private String questionText;

    /**
     * 正确答案（也可以不传给前端，防止作弊，等前端交卷后由后端比对。这里暂时保留）
     */
    private String correctAnswer;

    /**
     * 最终打乱后的 4 个选项（包含 1 个正确和 3 个错误项）
     */
    private List<String> options;

    /**
     * 【可选保留】如果你之前已经写了 setEnglishWord，并且前端需要用到，可以保留这个字段
     */
    // private String englishWord;
}