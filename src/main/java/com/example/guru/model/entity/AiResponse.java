package com.example.guru.model.entity;

import com.example.guru.model.entity.enums.RoleAIMessage;
import com.example.guru.util.UnixTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "ai_response")
public class AiResponse {
    @Id
    private String id;

    private String object;

    @JsonProperty("created")
    @JsonDeserialize(using = UnixTimestampDeserializer.class)
    private Timestamp createdAt;

    private String model;

    @Embedded
    private TokenUsage usage;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ai_response_id")
    private List<ChoiceData> choices;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @Entity
    public static class ChoiceData {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long index;

        @Embedded
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Getter
    @Setter
    @Embeddable
    public static class TokenUsage {
        @JsonProperty("prompt_tokens")
        private Long promptTokens;

        @JsonProperty("completion_tokens")
        private Long completionTokens;

        @JsonProperty("total_tokens")
        private Long totalTokens;
    }

    @Getter
    @Setter
    @Embeddable
    public static class Message {
        @Enumerated(value = EnumType.STRING)
        private RoleAIMessage role;

        private String content;
    }
}
