package com.example.guru.mapper;

import com.example.guru.model.entity.Answer;
import com.example.guru.model.entity.Question;
import com.example.guru.model.entity.User;
import com.example.guru.model.entity.UserAnswer;
import com.example.guru.model.entity.enums.CategoryType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAnswerMapperTest {

    private final UserAnswerMapper mapper = Mappers.getMapper(UserAnswerMapper.class);

    @Test
    public void convert() {
        Answer answer = Answer.builder()
                .id(1L)
                .text("text")
                .isRight(true)
                .question(Question.builder()
                        .categoryType(CategoryType.JAVA)
                        .build())
                .build();

        User user = User.builder().build();

        UserAnswer result = mapper.convert(answer, user);

        assertEquals(answer.getIsRight(), result.getIsRight());
        assertEquals(user, result.getUser());
        assertEquals(answer.getQuestion(), result.getQuestion());
        assertEquals(answer, result.getAnswer());

    }
}
