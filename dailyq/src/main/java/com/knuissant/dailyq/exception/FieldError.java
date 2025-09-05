package com.knuissant.dailyq.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldError {

  private final String field;
  private final String value;
  private final String reason;
}
