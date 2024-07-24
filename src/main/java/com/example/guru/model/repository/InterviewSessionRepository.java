package com.example.guru.model.repository;

import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.enums.InterviewType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    Optional<InterviewSession> findAllByUserAndInterviewTypeAndIsFinishedFalse(User user, InterviewType interviewType);
}
