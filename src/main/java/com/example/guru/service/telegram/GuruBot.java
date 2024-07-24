package com.example.guru.service.telegram;

import com.example.guru.model.entity.InterviewSession;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.enums.InterviewType;
import com.example.guru.service.InterviewService;
import com.example.guru.service.InterviewServiceHandler;
import com.example.guru.service.UserDataService;
import com.example.guru.util.CallBackDataHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RequiredArgsConstructor
@Component
public class GuruBot implements LongPollingSingleThreadUpdateConsumer, SpringLongPollingBot {
    private static final String CALLBACK_DELIMITER = "|";

    private final TelegramClient telegramClient;
    private final UserDataService userDataService;
    private final InterviewServiceHandler interviewServiceHandler;


    @Value("${telegram.token}")
    private String token;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")){
            userDataService.saveData(update);
            SendMessage sendMessage = SendMessage
                    .builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Choose the interview mode")
                    .replyMarkup(getStartedPanel())
                    .build();
            executeMessage(sendMessage);
        } else if (update.hasCallbackQuery()) {
            SendMessage sendMessage;
            String[] callBackData = CallBackDataHelper.parseCallBack(update.getCallbackQuery());

            InterviewService interviewService = interviewServiceHandler.getInterviewService(callBackData[1]);
            User user = userDataService.getByUserName(update.getCallbackQuery().getFrom().getUserName());
            InterviewSession session = interviewService.getOpenedSessionByUserAndInterviewType(user, interviewService.getInterviewServiceType())
                    .orElseGet(() -> interviewService.openInterview(user.getChat(), user));


            if (callBackData[0].equals("-1")) {
                sendMessage = interviewService.generateQuestion(session);
                sendMessage.setText("\n\n Ok! Let`start: \n\n\n" + sendMessage.getText());
            } else if (callBackData[2].equals("-1")) {
                interviewService.finishInterview(session);
                sendMessage = SendMessage.builder()
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .text("Thanks for the game! You were awesome \n\n\n" + interviewService.getInterviewStatistic(session))
                        .build();
            } else {
                String result = interviewService.checkAnswer(callBackData[2], session);
                sendMessage = interviewService.generateQuestion(session);
                sendMessage.setText(result + sendMessage.getText());
            }
            executeMessage(sendMessage);
        } else if (!update.hasCallbackQuery() && update.hasMessage()){
            SendMessage sendMessage;
            InterviewService interviewService = interviewServiceHandler.getInterviewService(InterviewType.INTERACTIVE_AI_MODE.name());

            User user = userDataService.getByUserName(update.getMessage().getFrom().getUserName());
            InterviewSession interviewSession = interviewService.getOpenedSessionByUserAndInterviewType(user, interviewService.getInterviewServiceType())
                    .orElseGet(() -> interviewService.openInterview(user.getChat(), user));

            String s = interviewService.checkAnswer(update.getMessage().getText(), interviewSession);
            sendMessage = interviewService.generateQuestion(interviewSession);
            sendMessage.setText(s + "\n\n" + sendMessage.getText());
            executeMessage(sendMessage);

        }
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private InlineKeyboardMarkup getStartedPanel() {
        return InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                .text(InterviewType.QUIZ_MODE.name())
                                .callbackData("-1" + CALLBACK_DELIMITER + InterviewType.QUIZ_MODE.name() + CALLBACK_DELIMITER)
                                .build()))
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                .text(InterviewType.INTERACTIVE_AI_MODE.name())
                                .callbackData("-1" + CALLBACK_DELIMITER + InterviewType.INTERACTIVE_AI_MODE.name() + CALLBACK_DELIMITER)
                                .build()))
                        .build();
    }
}
