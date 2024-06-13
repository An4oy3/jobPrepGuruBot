package com.example.guru.service;

import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface InterviewService {

    InterviewSession openInterview(Chat chat, User user);

    SendMessage generateQuestion(Long interviewSessionId);

    String checkAnswer(Long answerId, Long interviewSessionId);

    String getInterviewStatistic(InterviewSession interviewSession);

    void finishInterview(Long interviewSessionId);
}
