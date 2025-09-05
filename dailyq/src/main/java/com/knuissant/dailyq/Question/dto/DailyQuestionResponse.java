package com.knuissant.dailyq.Question.dto;

import com.knuissant.dailyq.Question.enums.Mode;
import java.time.LocalDate;
import java.util.List;

public class DailyQuestionResponse {
    private LocalDate date;
    private Mode mode;
    private List<QuestionResponse> items;

    public DailyQuestionResponse(LocalDate date, Mode mode, List<QuestionResponse> items) {
        this.date = date;
        this.mode = mode;
        this.items = items;
    }

    public LocalDate getDate() { return date; }
    public Mode getMode() { return mode; }
    public List<QuestionResponse> getItems() { return items; }
}


