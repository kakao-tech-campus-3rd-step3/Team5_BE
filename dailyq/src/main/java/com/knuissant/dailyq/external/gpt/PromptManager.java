package com.knuissant.dailyq.external.gpt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;

@Component
public class PromptManager {

    public String load(PromptType promptType) {
        try {
            String path = "prompts/" + promptType.getFileName();
            return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InfraException(ErrorCode.FILE_IO_ERROR);
        }
    }

    public String load(PromptType promptType, Object... args) {
        String template = load(promptType);
        return String.format(template, args);
    }

}
