package com.example.guru.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void convert() {
        User user = new User(1L, "firstName", false);
        user.setLastName("lastName");
        user.setLanguageCode("ru");
        user.setUserName("userName");
        user.setIsPremium(false);

        Chat chat = new Chat(1L, "GROUP");

        com.example.guru.model.entity.User result = userMapper.convert(user, chat);

        assertEquals(user.getUserName(), result.getUserName());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getLanguageCode(), result.getLanguageCode());
        assertEquals(user.getIsPremium(), result.getIsPremium());
        assertEquals(chat.getId(), result.getChat().getId());
        assertEquals(chat.getType(), result.getChat().getType().name());

    }

    @Test
    public void convertFromChat() {
        Chat chat = new Chat(1L, "GROUP");
        chat.setUserName("userName");
        chat.setFirstName("firstName");
        chat.setLastName("lastName");
        chat.setIsForum(false);
        chat.setTitle("title");
        chat.setDescription("description");
        chat.setInviteLink("inviteLink");

        com.example.guru.model.entity.User result = userMapper.convertFromChat(chat);

        assertEquals(chat.getUserName(), result.getUserName());
        assertEquals(chat.getFirstName(), result.getFirstName());
        assertEquals(chat.getLastName(), result.getLastName());
        assertNull(result.getLanguageCode());
        assertNull(result.getIsPremium());
        assertEquals(chat.getId(), result.getChat().getId());
        assertEquals(chat.getType(), result.getChat().getType().name());
    }
}
