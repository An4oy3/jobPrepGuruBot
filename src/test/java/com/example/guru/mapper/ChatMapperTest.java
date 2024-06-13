package com.example.guru.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.telegram.telegrambots.meta.api.objects.Chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatMapperTest {

    private final ChatMapper mapper = Mappers.getMapper(ChatMapper.class);

    @Test
    public void convert() {
        Chat chat = new Chat(1L, "SUPERGROUP");
        chat.setUserName("userName");
        chat.setFirstName("firstName");
        chat.setLastName("lastName");
        chat.setIsForum(false);
        chat.setTitle("title");
        chat.setDescription("description");
        chat.setInviteLink("inviteLink");

        com.example.guru.model.entity.Chat result = mapper.convert(chat);

        assertEquals(chat.getId(), result.getId());
        assertEquals(chat.getType(), result.getType().name());
        assertEquals(chat.getUserName(), result.getUserName());
        assertEquals(chat.getFirstName(), result.getFirstName());
        assertEquals(chat.getLastName(), result.getLastName());
        assertEquals(chat.getIsForum(), result.getIsForum());
        assertEquals(chat.getTitle(), result.getTitle());
        assertEquals(chat.getDescription(), result.getDescription());
        assertEquals(chat.getInviteLink(), result.getInviteLink());

    }
}
