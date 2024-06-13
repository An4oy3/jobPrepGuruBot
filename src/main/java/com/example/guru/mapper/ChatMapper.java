package com.example.guru.mapper;

import com.example.guru.model.entity.Chat;
import com.example.guru.model.entity.enums.ChatType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = ChatType.class)
public interface ChatMapper {

    @Mapping(target = "type", expression = "java(ChatType.getFromName(chat.getType()))")
    Chat convert(org.telegram.telegrambots.meta.api.objects.Chat chat);

}
