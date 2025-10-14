package com.knuissant.dailyq.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiApi openAiApi(@Value("${spring.ai.openai.api-key}") String apiKey) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(10));

        RestClient.Builder restClientBuilder = RestClient.builder().requestFactory(factory);

        return OpenAiApi.builder()
                .apiKey(apiKey)
                .restClientBuilder(restClientBuilder)
                .build();
    }

    @Bean
    public ChatClient chatClient(OpenAiApi openAiApi) {

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .build();

        return ChatClient.builder(chatModel).build();
    }
}
