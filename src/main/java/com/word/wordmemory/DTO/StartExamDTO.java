package com.word.wordmemory.DTO;

import lombok.Data;

@Data
public class StartExamDTO {
    private Long bookId;
    private Integer examCount = 20;
    private Double enToZhRatio = 0.5;
    private Integer setTime = 10;
}