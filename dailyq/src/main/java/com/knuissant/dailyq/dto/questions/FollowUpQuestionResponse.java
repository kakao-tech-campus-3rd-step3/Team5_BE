package com.knuissant.dailyq.dto.questions;

import java.util.List;

public record FollowUpQuestionResponse(
    List<String> questions
) {
}
