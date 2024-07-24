package com.example.guru.service;

import com.example.guru.exception.AnswerNotFoundException;
import com.example.guru.exception.InterviewSessionNotFoundException;
import com.example.guru.mapper.UserAnswerMapper;
import com.example.guru.model.entity.Answer;
import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.Question;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.enums.CategoryType;
import com.example.guru.model.entity.enums.InterviewType;
import com.example.guru.model.repository.AnswerRepository;
import com.example.guru.model.repository.InterviewSessionRepository;
import com.example.guru.model.repository.QuestionRepository;
import com.example.guru.model.repository.UserAnswerRepository;
import com.example.guru.util.CallBackDataHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizInterviewServiceImpl implements InterviewService {

    private final InterviewSessionRepository interviewSessionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserAnswerMapper userAnswerMapper;


    @Override
    public InterviewSession openInterview(Chat chat, User user) {
        InterviewSession newSession = InterviewSession.builder()
                .rightAnswers(0)
                .wrongAnswers(0)
                .chat(chat)
                .user(user)
                .correctAnswersRate(0.0)
                .createdAt(LocalDateTime.now())
                .interviewCategory(CategoryType.JAVA)
                .interviewType(InterviewType.QUIZ_MODE)
                .questions(Collections.emptyList())
                .isFinished(false)
                .build();
        interviewSessionRepository.save(newSession);
        return newSession;
    }

    @Override
    public SendMessage generateQuestion(InterviewSession interviewSession) {
        List<Question> availableQuestions;
        if (interviewSession.getQuestions().isEmpty()) {
            availableQuestions = questionRepository.findByCategoryType(interviewSession.getInterviewCategory());
        } else {
            availableQuestions = questionRepository.findByCategoryAndNotIn(
                    interviewSession.getInterviewCategory(),
                    interviewSession.getQuestions().stream()
                            .map(Question::getId)
                            .toList());
        }

        if (availableQuestions.isEmpty()) {
            finishInterview(interviewSession);
            return SendMessage.builder()
                    .chatId(interviewSession.getChat().getId())
                    .text(" You`ve answered all questions. Congratulations! \n " +
                            getInterviewStatistic(interviewSession))
                    .build();
        } else {
            Collections.shuffle(availableQuestions);
            Question nextQuestion = availableQuestions.get(0);
            interviewSession.setLastQuestion(nextQuestion);
            interviewSessionRepository.save(interviewSession);
            return buildSendMessage(interviewSession, nextQuestion);
        }
    }

    @Override
    public String checkAnswer(String answerData, InterviewSession interviewSession) {
        Answer answer = answerRepository.findById(Long.parseLong(answerData))
                .orElseThrow(() -> new AnswerNotFoundException("Answer with id: \"" + answerData + "\" not found"));

        userAnswerRepository.save(userAnswerMapper.convert(answer, interviewSession.getUser()));

        List<Question> prevQuestions = interviewSession.getQuestions();
        prevQuestions.add(answer.getQuestion());

        interviewSession.setQuestions(prevQuestions);

        if (answer.isRight()) {
            interviewSession.setRightAnswers(interviewSession.getRightAnswers() + 1);
            interviewSession.setCorrectAnswersRate((double) ((interviewSession.getRightAnswers() / prevQuestions.size()) * 100));
            interviewSessionRepository.save(interviewSession);
            return "\n\n\n Bingo! Your answer is correct! You are simply a genius! \n\n\n\n";
        } else {
            interviewSession.setWrongAnswers(interviewSession.getWrongAnswers() + 1);
            interviewSession.setCorrectAnswersRate(( (double) interviewSession.getRightAnswers() / prevQuestions.size()) * 100);
            interviewSessionRepository.save(interviewSession);
            return "\n\n\n Ah, missed! But you're still great! \n\n\n\n";
        }
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
        return InterviewType.QUIZ_MODE;
    }

    private SendMessage buildSendMessage(InterviewSession interviewSession, Question nextQuestion) {
        Map<Integer, Answer> optionAnswerMap = new HashMap<>();
        List<Answer> options = new ArrayList<>();
        nextQuestion.getWrongAnswers().stream()
                .limit(3)
                .forEach(options::add);
        nextQuestion.getRightAnswers().stream()
                .limit(4 - options.size())
                .forEach(options::add);

        Collections.shuffle(options);
        for (int i = 1; i < options.size() + 1; i++) {
            optionAnswerMap.put(i, options.get(i - 1));
        }


        return SendMessage.builder()
                .chatId(interviewSession.getChat().getId())
                .text(" The next question is : \n" + nextQuestion.getText() + "\n\n\n" + "Answer options: \n\n\n" + buildAnswerOptions(optionAnswerMap))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buildKeyboard(interviewSession, optionAnswerMap))
                        .build())
                .build();
    }

    private List<InlineKeyboardRow> buildKeyboard(InterviewSession interviewSession, Map<Integer, Answer> optionAnswerMap) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        optionAnswerMap.forEach((key, value) ->
                rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text(key.toString())
                        .callbackData(CallBackDataHelper.buildCallBackMsg(interviewSession.getId(), interviewSession.getInterviewType(), value.getId().toString()))
                        .build())));

        rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                .text("Finish")
                .callbackData(CallBackDataHelper.buildCallBackMsg(interviewSession.getId(), interviewSession.getInterviewType(), "-1"))
                .build()));

        return rows;
    }

    private String buildAnswerOptions(Map<Integer, Answer> optionAnswerMap) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, Answer> entry : optionAnswerMap.entrySet()) {
            result.append(entry.getKey()).append(". ").append(entry.getValue().getText()).append("\n");
        }
        return result.toString();
    }
}
