package com.example.guru.model.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleAIMessage {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool"),
    FUNCTION("function");

    private final String value;

    RoleAIMessage(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
