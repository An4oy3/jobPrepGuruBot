package com.example.guru.service;

import com.example.guru.exception.InterviewSessionNotFoundException;
import com.example.guru.model.entity.AiMessage;
import com.example.guru.model.entity.Answer;
import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.Question;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.UserAnswer;
import com.example.guru.model.entity.enums.CategoryType;
import com.example.guru.model.entity.enums.InterviewType;
import com.example.guru.model.repository.AnswerRepository;
import com.example.guru.model.repository.InterviewSessionRepository;
import com.example.guru.model.repository.QuestionRepository;
import com.example.guru.model.repository.UserAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractiveInterviewServiceImpl implements InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final AIService aiService;
    private final UserAnswerRepository userAnswerRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;


    @Override
    public InterviewSession openInterview(Chat chat, User user) {
        InterviewSession interviewSession = InterviewSession.builder()
                .rightAnswers(0)
                .wrongAnswers(0)
                .chat(chat)
                .user(user)
                .correctAnswersRate(0.0)
                .createdAt(LocalDateTime.now())
                .interviewCategory(CategoryType.JAVA)
                .interviewType(InterviewType.INTERACTIVE_AI_MODE)
                .questions(Collections.emptyList())
                .isFinished(false)
                .build();
        interviewSessionRepository.save(interviewSession);
        return interviewSession;
    }

    @Override
    public SendMessage generateQuestion(InterviewSession interviewSession) {
        List<Question> availableQuestions = interviewSession.getQuestions().isEmpty()
                ? questionRepository.findByCategoryType(interviewSession.getInterviewCategory())
                : questionRepository.findByCategoryAndNotIn(
                        interviewSession.getInterviewCategory(),
                        interviewSession.getQuestions().stream()
                                .map(Question::getId)
                                .toList());

        if (availableQuestions.isEmpty()) {
            finishInterview(interviewSession);
            return SendMessage.builder()
                    .chatId(interviewSession.getChat().getId())
                    .text(" You`ve answered all questions. Congratulations! \n " +
                            getInterviewStatistic(interviewSession))
                    .build();
        }

        Collections.shuffle(availableQuestions);
        Question nextQuestion = availableQuestions.get(0);
        interviewSession.setLastQuestion(nextQuestion);
        interviewSessionRepository.save(interviewSession);
        return SendMessage.builder()
                .chatId(interviewSession.getChat().getId())
                .text(" The next question is : \n" + nextQuestion.getText() + "\n\n\n" + "Please, text your answer to the chat!")
                .build();
    }

    @Transactional
    @Override
    public String checkAnswer(String answerData, InterviewSession interviewSession) {
        AiMessage answerStatus = aiService.checkUserAnswer(interviewSession.getLastQuestion(), answerData);
        Answer answer = answerRepository.save(Answer.builder()
                .text(answerData)
                .question(interviewSession.getLastQuestion())
                .isRight(answerStatus.getIsRight())
                .build());
        userAnswerRepository.save(UserAnswer.builder()
                .answer(answer)
                .dateTime(LocalDateTime.now())
                .isRight(answer.isRight())
                .question(interviewSession.getLastQuestion())
                .user(interviewSession.getUser())
                .build());

        List<Question> prevQuestions = interviewSession.getQuestions();
        prevQuestions.add(interviewSession.getLastQuestion());

        interviewSession.setQuestions(prevQuestions);

        if (answer.isRight()) {
            interviewSession.setRightAnswers(interviewSession.getRightAnswers() + 1);
            interviewSession.setCorrectAnswersRate((double) ((interviewSession.getRightAnswers() / prevQuestions.size()) * 100));
            return "\n\n\n " + answerStatus.getContent() + " \n\n\n\n";
        }
        interviewSession.setWrongAnswers(interviewSession.getWrongAnswers() + 1);
        interviewSession.setCorrectAnswersRate(( (double) interviewSession.getRightAnswers() / prevQuestions.size()) * 100);
        return "\n\n\n Ah, missed! But you're still great! \n\n\n" + answerStatus.getContent();
    }

    @Override
    public void finishInterview(InterviewSession interviewSession) {
        interviewSession.setIsFinished(true);
        interviewSession.setCorrectAnswersRate((double) interviewSession.getRightAnswers() / interviewSession.getQuestions().size() * 100);
        interviewSessionRepository.save(interviewSession);
    }

    @Override
    public Optional<InterviewSession> getOpenedSessionByUserAndInterviewType(User user, InterviewType interviewType) {
        return interviewSessionRepository.findAllByUserAndInterviewTypeAndIsFinishedFalse(user, interviewType);
    }

    @Override
    public InterviewType getInterviewServiceType() {
        return InterviewType.INTERACTIVE_AI_MODE;
    }
}
