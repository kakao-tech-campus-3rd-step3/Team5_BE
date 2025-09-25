package com.knuissant.dailyq.dto.questions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpQuestionResponse {

    @JsonProperty("questions")
    private List<String> questions;
}
