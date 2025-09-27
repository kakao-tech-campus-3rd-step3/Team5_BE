package com.knuissant.dailyq.external.gpt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromptType {
    FEEDBACK_SYSTEM("feedback_system.prompt"),
    FEEDBACK_USER("feedback_user.prompt"),
    FOLLOWUP_SYSTEM("followup_system.prompt"),
    FOLLOWUP_USER("followup_user.prompt");

    private final String fileName;
}
