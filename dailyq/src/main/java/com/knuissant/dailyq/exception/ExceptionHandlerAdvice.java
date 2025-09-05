package com.knuissant.dailyq.exception;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
    ErrorCode errorCode = ex.getErrorCode();
    ProblemDetail problemDetail = errorCode.toProblemDetail();
    log.warn("BusinessException : {}", ex.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {

    ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
    ProblemDetail problemDetail = errorCode.toProblemDetail();

    List<FieldError> validationErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(error -> new FieldError(
            error.getField(),
            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
            error.getDefaultMessage()
        ))
        .collect(Collectors.toList());

    problemDetail.setProperty("validationErrors", validationErrors);

    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUncaughtException(Exception ex) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    ProblemDetail problemDetail = errorCode.toProblemDetail();

    log.error("예상치 못한 에러 발생 : ", ex);

    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }
}

