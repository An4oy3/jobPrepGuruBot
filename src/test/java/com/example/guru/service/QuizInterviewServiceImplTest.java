package com.example.guru.service;

import com.example.guru.mapper.UserAnswerMapper;
import com.example.guru.model.entity.Answer;
import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.Question;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.UserAnswer;
import com.example.guru.model.entity.enums.CategoryType;
import com.example.guru.model.entity.enums.ChatType;
import com.example.guru.model.entity.enums.InterviewType;
import com.example.guru.model.repository.AnswerRepository;
import com.example.guru.model.repository.InterviewSessionRepository;
import com.example.guru.model.repository.QuestionRepository;
import com.example.guru.model.repository.UserAnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuizInterviewServiceImplTest {

    private InterviewSessionRepository interviewSessionRepository;
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private UserAnswerRepository userAnswerRepository;
    private UserAnswerMapper userAnswerMapper;

    private QuizInterviewServiceImpl quizInterviewServiceImpl;


    private InterviewSession interviewSession;
    private User user;
    private Chat chat;
    private Question question;
    private Answer answer;

    @BeforeEach
    public void init() {
        interviewSessionRepository = mock(InterviewSessionRepository.class);
        questionRepository = mock(QuestionRepository.class);
        answerRepository = mock(AnswerRepository.class);
        userAnswerRepository = mock(UserAnswerRepository.class);
        userAnswerMapper = mock(UserAnswerMapper.class);

        quizInterviewServiceImpl = new QuizInterviewServiceImpl(
                interviewSessionRepository,
                questionRepository,
                answerRepository,
                userAnswerRepository,
                userAnswerMapper);

        question = Question.builder()
                .id(1L)
                .categoryType(CategoryType.JAVA)
                .text("text")
                .answers(List.of(Answer.builder().id(123L).build()))
                .build();

        answer = Answer.builder()
                .id(1L)
                .isRight(true)
                .question(question)
                .text("text")
                .build();

        user = User.builder()
                .id(1L)
                .chat(chat)
                .userName("userName")
                .build();

        chat = Chat.builder()
                .id(1L)
                .title("title")
                .type(ChatType.PRIVATE)
                .userName("chatUserName")
                .user(user)
                .build();

        interviewSession = InterviewSession.builder()
                .id(1L)
                .interviewType(InterviewType.QUIZ_MODE)
                .isFinished(false)
                .interviewCategory(CategoryType.JAVA)
                .user(user)
                .chat(chat)
                .createdAt(LocalDateTime.now())
                .questions(List.of(question))
                .rightAnswers(1)
                .wrongAnswers(0)
                .build();
    }


    @Test
    public void openSessionTest() {
        InterviewSession result = quizInterviewServiceImpl.openInterview(chat, user);

        assertEquals(0, result.getRightAnswers().intValue());
        assertEquals(0, result.getWrongAnswers().intValue());
        assertEquals(0, result.getCorrectAnswersRate().intValue());
        assertEquals(CategoryType.JAVA, result.getInterviewCategory());
        assertEquals(InterviewType.QUIZ_MODE, result.getInterviewType());
        assertFalse(result.getIsFinished());
        assertEquals(chat, result.getChat());
        assertEquals(user, result.getUser());

        verify(interviewSessionRepository, times(1)).save(result);
    }

    @Test
    public void generateQuestionTest() {
        when(interviewSessionRepository.findById(1L)).thenReturn(Optional.of(interviewSession));
        when(questionRepository.findByCategoryAndNotIn(any(), any())).thenReturn(List.of(question));

        SendMessage result = quizInterviewServiceImpl.generateQuestion(1L);

        assertEquals(interviewSession.getChat().getId().toString(), result.getChatId());
        assertEquals("The next question is : \n" + question.getText(), result.getText());
    }

    @Test
    public void generateCongratulationMessage() {
        when(interviewSessionRepository.findById(1L)).thenReturn(Optional.of(interviewSession));
        when(questionRepository.findByCategoryType(question.getCategoryType())).thenReturn(List.of());

        SendMessage badResult = quizInterviewServiceImpl.generateQuestion(1L);

        assertEquals(interviewSession.getChat().getId().toString(), badResult.getChatId());
        assertTrue(badResult.getText().contains("You`ve answered all questions. Congratulations! \n "));
    }

    @Test
    public void getInterviewStatistic() {
        when(interviewSessionRepository.findById(interviewSession.getId())).thenReturn(Optional.of(interviewSession));

        String result = quizInterviewServiceImpl.getInterviewStatistic(interviewSession.getId());

        assertEquals(" Statistics: \n" + "Total Questions: " + interviewSession.getQuestions().size() + "\n" +
                "Correct answers: " + interviewSession.getRightAnswers() + "\n" +
                "Wrong Answers: " + interviewSession.getWrongAnswers() + "\n" +
                "Correct answers rate: " + interviewSession.getCorrectAnswersRate(),
                result);
    }

    @Test
    public void finishInterviewTest() {
        when(interviewSessionRepository.findById(1L)).thenReturn(Optional.of(interviewSession));

        quizInterviewServiceImpl.finishInterview(1L);
        assertTrue(interviewSession.getIsFinished());
        verify(interviewSessionRepository, times(1)).save(interviewSession);
    }

    @Test
    public void checkBingoAnswerTest() {
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        interviewSession.setQuestions(questions);

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(interviewSessionRepository.findById(interviewSession.getId())).thenReturn(Optional.of(interviewSession));
        when(userAnswerMapper.convert(answer, user)).thenReturn(UserAnswer.builder().build());

        String result = quizInterviewServiceImpl.checkAnswer(1L, 1L);

        verify(userAnswerRepository, times(1)).save(any());

        assertEquals(2, interviewSession.getQuestions().size());
        assertEquals(2, interviewSession.getRightAnswers().intValue());
        assertEquals(0, interviewSession.getWrongAnswers().intValue());
        assertEquals(Optional.of(100.0), Optional.of(interviewSession.getCorrectAnswersRate()));
        assertEquals("\n\n\n Bingo! Your answer is correct! You are simply a genius! \n\n\n\n", result);
    }

    @Test
    public void checkMissedAnswerTest() {
        answer.setRight(false);
        List<Question> questions = new ArrayList<>();
        questions.add(question);
        interviewSession.setQuestions(questions);


        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        when(interviewSessionRepository.findById(interviewSession.getId())).thenReturn(Optional.of(interviewSession));
        when(userAnswerMapper.convert(answer, user)).thenReturn(UserAnswer.builder().build());

        String result = quizInterviewServiceImpl.checkAnswer(1L, 1L);

        verify(userAnswerRepository, times(1)).save(any());
        assertEquals(2, interviewSession.getQuestions().size());
        assertEquals(1, interviewSession.getRightAnswers().intValue());
        assertEquals(1, interviewSession.getWrongAnswers().intValue());
        assertEquals(Optional.of(50.0), Optional.of(interviewSession.getCorrectAnswersRate()));
        assertEquals("\n\n\n Ah, missed! But you're still great!", result);
    }
}
