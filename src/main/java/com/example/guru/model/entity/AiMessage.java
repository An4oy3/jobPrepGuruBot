package com.example.guru.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ai_message_id_seq")
    @SequenceGenerator(name = "ai_message_id_seq", allocationSize = 1)
    private Long id;

    @JsonProperty(value = "IsRight")
    private Boolean isRight;

    @JsonProperty(value = "Content")
    private String content;

    private String userAnswer;

    @OneToOne
    private AiResponse aiResponse;

    @OneToOne
    private Question question;
}
