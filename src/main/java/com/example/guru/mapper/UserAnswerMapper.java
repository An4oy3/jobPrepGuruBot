package com.example.guru.mapper;

import com.example.guru.model.entity.Answer;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.UserAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = LocalDateTime.class)
public interface UserAnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateTime", expression = "java(LocalDateTime.now())")
    @Mapping(target = "answer", source = "answer")
    @Mapping(target = "isRight", expression = "java(answer.isRight())")
    UserAnswer convert(Answer answer, User user);
}
