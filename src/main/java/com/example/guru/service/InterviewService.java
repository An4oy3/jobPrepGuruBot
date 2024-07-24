package com.example.guru.service;

import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.enums.InterviewType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

public interface InterviewService {

    InterviewSession openInterview(Chat chat, User user);

    SendMessage generateQuestion(InterviewSession interviewSession);

    String checkAnswer(String answerData, InterviewSession interviewSession);

    void finishInterview(InterviewSession interviewSession);

    Optional<InterviewSession> getOpenedSessionByUserAndInterviewType(User user, InterviewType interviewType);

    InterviewType getInterviewServiceType();

    default String getInterviewStatistic(InterviewSession interviewSession) {
        return " Statistics: \n" + "Total Questions: " + interviewSession.getQuestions().size() + "\n" +
                "Correct answers: " + interviewSession.getRightAnswers() + "\n" +
                "Wrong Answers: " + interviewSession.getWrongAnswers() + "\n" +
                "Correct answers rate: " + interviewSession.getCorrectAnswersRate();
    }
}
