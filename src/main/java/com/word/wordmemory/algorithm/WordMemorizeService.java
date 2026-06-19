package com.word.wordmemory.algorithm;

import com.word.wordmemory.entity.Word;
import java.util.*;

public class WordMemorizeService {


    public List<Word> generateWordList(List<WordWithStatus> rawList, int needCount) {
        List<Word> drawPool = new ArrayList<>();

        // 1. 严格按照需求装填频率
        for (WordWithStatus item : rawList) {
            Integer status = item.getStatus();
            if (status == null) status = 0; // 没背过的默认为未记

            if (status == 2) {
                continue; // 记得的不再出现
            } else if (status == 1) {
                drawPool.add(item.getWord()); // 模糊出现1次
            } else if (status == 0) {
                drawPool.add(item.getWord()); // 未记得出现2次
                drawPool.add(item.getWord());
            }
        }

        // 2. 初始洗牌
        Collections.shuffle(drawPool);

        // 3. 截取所需数量
        int toIndex = Math.min(needCount, drawPool.size());
        List<Word> resultList = new ArrayList<>(drawPool.subList(0, toIndex));

        // 4. 亮点：滑动窗口防重叠（防止同一个单词挨在一起）
        for (int i = 0; i < resultList.size() - 1; i++) {
            if (resultList.get(i).getId().equals(resultList.get(i + 1).getId())) {
                // 发现重复，在后面找一个不一样的单词跟它换位置
                for (int j = i + 2; j < resultList.size(); j++) {
                    if (!resultList.get(j).getId().equals(resultList.get(i).getId())) {
                        Collections.swap(resultList, i + 1, j);
                        break;
                    }
                }
            }
        }

        return resultList;
    }
}