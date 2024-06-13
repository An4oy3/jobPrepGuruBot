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

            if (callBackData[0].equals("-1")) {
                User user = userDataService.getByUserName(update.getCallbackQuery().getFrom().getUserName());
                InterviewSession newSession = interviewService.openInterview(user.getChat(), user);
                sendMessage = interviewService.generateQuestion(newSession.getId());
                sendMessage.setText("\n\n\n Ok! Let`start new interview: \n" + sendMessage.getText());
            } else {
                String result = interviewService.checkAnswer(Long.parseLong(callBackData[2]), Long.parseLong(callBackData[0]));
                sendMessage = interviewService.generateQuestion(Long.parseLong(callBackData[0]));
                sendMessage.setText(result + sendMessage.getText());
            }
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
                                .text(InterviewType.COMPETITIVE_MODE.name())
                                .callbackData("-1" + CALLBACK_DELIMITER + InterviewType.COMPETITIVE_MODE.name() + CALLBACK_DELIMITER)
                                .build()))
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                .text(InterviewType.QUIZ_MODE.name())
                                .callbackData("-1" + CALLBACK_DELIMITER + InterviewType.QUIZ_MODE.name() + CALLBACK_DELIMITER)
                                .build()))
                        .keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                .text(InterviewType.CREATIVITY_MODE.name())
                                .callbackData("-1" + CALLBACK_DELIMITER + InterviewType.CREATIVITY_MODE.name() + CALLBACK_DELIMITER)
                                .build()))
                        .build();
    }
}
