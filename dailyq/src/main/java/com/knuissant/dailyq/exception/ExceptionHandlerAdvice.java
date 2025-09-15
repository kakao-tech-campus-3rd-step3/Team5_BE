package com.knuissant.dailyq.exception;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex, HttpServletRequest request) {
    ErrorCode errorCode = ex.getErrorCode();
    ProblemDetail problemDetail = errorCode.toProblemDetail();
    log.warn("BusinessException URI : {}, Code : {}, Message : {}", request.getRequestURI(),errorCode.getCode(),errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
    ProblemDetail problemDetail = errorCode.toProblemDetail();

    List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(error -> {
          Object rejected = error.getRejectedValue();
          return new ValidationError(
              error.getField(),
              rejected == null ? "" : rejected.toString(),
              error.getDefaultMessage()
          );
        })
        .collect(Collectors.toList());

    problemDetail.setProperty("validationErrors", validationErrors);

    log.warn("Validation Fail URI : {}, Code : {}, Message : {}", request.getRequestURI(),errorCode.getCode(),errorCode.getMessage());

    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUncaughtException(Exception ex,HttpServletRequest request) {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    ProblemDetail problemDetail = errorCode.toProblemDetail();

    log.error("Unpredicted Error URI : {}, Method : {} ", request.getRequestURI(), request.getRequestURI(), ex);

    return ResponseEntity.status(errorCode.getStatus()).body(problemDetail);
  }
}

