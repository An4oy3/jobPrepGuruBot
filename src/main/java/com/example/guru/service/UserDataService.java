package com.example.guru.service;

import com.example.guru.exception.UserNotFoundException;
import com.example.guru.mapper.UserMapper;
import com.example.guru.model.entity.User;
import com.example.guru.model.repository.ChatRepository;
import com.example.guru.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Service
public class UserDataService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserMapper userMapper;

    public void saveData(Update update) {
        if (!update.hasMessage() || (update.getMessage().getChat() == null && update.getMessage().getFrom() == null)) {
            return;
        }

        org.telegram.telegrambots.meta.api.objects.Chat updateChat = update.getMessage().getChat();
        org.telegram.telegrambots.meta.api.objects.User updateUser = update.getMessage().getFrom();

        if (updateUser != null && userRepository.findByUserName(updateUser.getUserName()).isEmpty()) {
            userRepository.save(userMapper.convert(updateUser, updateChat));
        } else if (updateChat != null && chatRepository.findById(updateChat.getId()).isEmpty()) {
            userRepository.save(userMapper.convertFromChat(updateChat));
        }
    }

    public User getByUserName(String username) {
        return userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found."));
    }
}
