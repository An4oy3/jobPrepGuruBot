package com.example.guru.model.entity.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum InterviewType {
    QUIZ_MODE("quiz", "Question-answer format. A question with several options, one of which is correct."),
    COMPETITIVE_MODE("quizUntilWrong",
            "Question-answer format. A question with several options, one of which is correct. " +
                    "The interview continues until the first incorrect answer is given."),
    INTERACTIVE_AI_MODE("creativeQuiz", "");


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
