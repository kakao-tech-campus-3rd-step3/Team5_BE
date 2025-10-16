package com.knuissant.dailyq.external.gpt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;

@Component
public class PromptManager {

    private final Map<PromptType, String> promptMap = new EnumMap<>(PromptType.class);

    @PostConstruct
    public void init() {
        for (PromptType promptType : PromptType.values()) {
            try {
                String path = "prompts/" + promptType.getFileName();
                String content = new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
                promptMap.put(promptType, content);
            } catch (IOException e) {
                throw new InfraException(ErrorCode.FILE_IO_ERROR, promptType.getFileName());
            }
        }
    }

    public String load(PromptType promptType) {
        String content = promptMap.get(promptType);
        if (content == null) {
            throw new InfraException(ErrorCode.PROMPT_NOT_FOUND_IN_CACHE, promptType.name());
        }
        return content;
    }

    public String load(PromptType promptType, Object... args) {
        String template = load(promptType);
        return String.format(template, args);
    }

}
