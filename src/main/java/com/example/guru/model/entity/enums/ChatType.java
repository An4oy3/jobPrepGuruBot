package com.example.guru.model.entity.enums;

import java.util.Arrays;

public enum ChatType {
    PRIVATE, GROUP, SUPERGROUP, CHANNEL;

    public static ChatType getFromName(String name) {
        return Arrays.stream(values())
                .filter(chatType ->  name.equalsIgnoreCase(chatType.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching chatType for : " + name));
    }
}
