package com.example.guru.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@Data
public class TelegramConfig {
    @Value("${telegram.token}")
    private String token;

    @Bean
    public TelegramClient getTelegramClient() {
        return new OkHttpTelegramClient(token);
    }
}
