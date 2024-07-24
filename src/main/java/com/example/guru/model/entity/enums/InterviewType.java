package com.example.guru.model.entity.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum InterviewType {
    QUIZ_MODE("quiz", "Question-answer format. A question with several options, one of which is correct."),
    INTERACTIVE_AI_MODE("aiMode", "Question-answer format. Prepared questions without options. " +
            "Players should give answers in free format. Then AI processes these answers and gives a feedback.");


    private final String name;
    private final String description;

    public static InterviewType getByName(String name) {
        return Arrays.stream(values())
                .filter(interviewType -> interviewType.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching interviewType for : " + name));
    }

    public static boolean contains(String name) {
        return Arrays.stream(values())
                .anyMatch(interviewType -> interviewType.name().equals(name));
    }

}
