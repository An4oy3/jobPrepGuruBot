package com.example.guru.mapper;

import com.example.guru.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {LocalDateTime.class, ChatMapper.class}, uses = {UserMapper.class})
public interface UserMapper {

    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "registrationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "chat", source = "chat", qualifiedByName = "chatConvert")
    User convert(org.telegram.telegrambots.meta.api.objects.User user, Chat chat);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "chat", source = "chat", qualifiedByName = "chatConvert")
    User convertFromChat(Chat chat);

    @Named("chatConvert")
    static com.example.guru.model.entity.Chat convertChat(Chat chat) {
        return INSTANCE.convert(chat);
    }
}
