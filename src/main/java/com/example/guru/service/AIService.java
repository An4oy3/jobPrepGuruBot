package com.example.guru.service;

import com.example.guru.model.entity.AiMessage;
import com.example.guru.model.entity.AiResponse;
import com.example.guru.model.entity.Question;
import com.example.guru.model.repository.AiMessageRepository;
import com.example.guru.model.repository.AiResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AIService {

    private static final String baseURL = "https://api.openai.com";
    @Value("${ai.token}")
    private String apiKey;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final AiResponseRepository aiResponseRepository;
    private final AiMessageRepository aiMessageRepository;


    public boolean testConnection() {
        String connectionStatus = "Connection was successful";
        String requestBody = "{\r\n" +
                "    \"model\": \"gpt-3.5-turbo-16k\",\r\n" +
                "    \"messages\": [\r\n" +
                "        {\r\n" +
                "            \"role\": \"system\",\r\n" +
                "            \"content\": \"You are a helpful assistant.\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"role\": \"user\",\r\n" +
                "            \"content\": \"Say '" + connectionStatus + "' if you received this message!\"\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}";


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBody);
        Request request = new Request.Builder()
                .url(baseURL + "/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.body() != null) {
                AiResponse aiResponse = new ObjectMapper().readValue(response.body().bytes(), AiResponse.class);
                return aiResponse.getChoices().stream()
                        .anyMatch(choiceData -> choiceData.getMessage().getContent().contains(connectionStatus));
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public AiMessage checkUserAnswer(Question question, String userAnswer) {
        AiMessage aiMessage = null;
        String requestBodyJson = "{\r\n" +
                "    \"model\": \"gpt-3.5-turbo-16k\",\r\n" +
                "    \"messages\": [\r\n" +
                "        {\r\n" +
                "            \"role\": \"system\",\r\n" +
                "            \"content\": \"You are a helpful assistant.\"\r\n" +
                "        },\r\n" +
                "        {\r\n" +
                "            \"role\": \"user\",\r\n" +
                "            \"content\": \"There is a question - '" + question.getText() + "' and an answer - '" + userAnswer + "'! Is is correct? Make your answer in JSON format with two fields. IsRight(boolean), Content(String). The second field should have your comment\"\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, requestBodyJson);
        Request request = new Request.Builder()
                .url(baseURL + "/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.body() != null) {
                AiResponse aiResponse = objectMapper.readValue(response.body().bytes(), AiResponse.class);
                aiResponseRepository.save(aiResponse);

                aiMessage = objectMapper.readValue(aiResponse.getChoices().get(0).getMessage().getContent(), AiMessage.class);
                aiMessage.setUserAnswer(userAnswer);
                aiMessage.setQuestion(question);
                aiMessage.setAiResponse(aiResponse);

                aiMessageRepository.save(aiMessage);
            }
            return aiMessage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Create method for generation questions with OpenAI
}
