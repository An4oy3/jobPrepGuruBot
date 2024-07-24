package com.example.guru.service;

import com.example.guru.model.entity.enums.InterviewType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class InterviewServiceHandler {

    private static final Map<InterviewType, InterviewService> interviewServiceMapRegister = new HashMap<>();

    private final QuizInterviewServiceImpl quizInterviewServiceImpl;
    private final InteractiveInterviewServiceImpl interactiveInterviewService;

    public InterviewService getInterviewService(String interviewType) {
        if (!StringUtils.hasLength(interviewType)) {
            throw new IllegalArgumentException("Parameter interviewType must have the content");
        }
        return interviewServiceMapRegister.getOrDefault(InterviewType.getByName(interviewType), quizInterviewServiceImpl);
    }

    @PostConstruct
    private void registerInit() {
        interviewServiceMapRegister.put(InterviewType.QUIZ_MODE, quizInterviewServiceImpl);
        interviewServiceMapRegister.put(InterviewType.INTERACTIVE_AI_MODE, interactiveInterviewService);
    }
}
