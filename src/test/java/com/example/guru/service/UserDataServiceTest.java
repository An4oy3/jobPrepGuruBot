package com.example.guru.service;

import com.example.guru.exception.UserNotFoundException;
import com.example.guru.mapper.UserMapper;
import com.example.guru.model.repository.ChatRepository;
import com.example.guru.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;

    private UserDataService userDataService;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        ChatRepository chatRepository = mock(ChatRepository.class);
        userMapper = mock(UserMapper.class);

        userDataService = new UserDataService(userRepository, chatRepository, userMapper);
    }

    @Test
    public void saveDataTest() {

        Update update = new Update();
        update.setMessage(Message.builder()
                .chat(new Chat(1L, "type"))
                .from(new User(1L, "username", false))
                .build());

        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userMapper.convert(any(), any())).thenReturn(new com.example.guru.model.entity.User());
        userDataService.saveData(update);

        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByUserName(update.getMessage().getFrom().getUserName());
        verify(userMapper, times(1)).convert(any(), any());
    }

    @Test
    public void getByUserNameTest() {
        com.example.guru.model.entity.User user = com.example.guru.model.entity.User.builder()
                .id(1L)
                .userName("userName")
                .build();

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        com.example.guru.model.entity.User rightResult = userDataService.getByUserName(user.getUserName());

        assertEquals(user, rightResult);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDataService.getByUserName("wrongUserName"));

        assertEquals("User with username wrongUserName not found.", exception.getMessage());
    }
}
