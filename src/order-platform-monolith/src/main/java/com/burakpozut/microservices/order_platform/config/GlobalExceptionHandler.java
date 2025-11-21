package com.burakpozut.microservices.order_platform.config;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.burakpozut.microservices.order_platform.common.api.ApiError;
import com.burakpozut.microservices.order_platform.common.exception.AppException;
import com.burakpozut.microservices.order_platform.common.exception.BusinessException;
import com.burakpozut.microservices.order_platform.common.exception.DomainValidationException;
import com.burakpozut.microservices.order_platform.common.exception.NotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.NOT_FOUND.value(),
        "NOT_FOUND",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);

  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.CONFLICT.value(),
        "BUSINESS_ERROR",
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ApiError> handleDomainValidation(DomainValidationException ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "DOMAIN_VALIDATION_ERROR",
        ex.getMessage());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiError> handleAppException(AppException ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "BAD_REQUEST",
        ex.getMessage());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ApiError> handleDatabaseError(DataAccessException ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "DATABASE_ERROR",
        "A database error occurred");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnkown(Exception ex) {
    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "INTERVAL_SERVER_ERROR",
        "Unexpected error occured");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);

  }

  // 1) Bean Validation for @RequestBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

    // collect field errors into a single message or list
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + " " + err.getDefaultMessage())
        .collect(Collectors.joining("; "));

    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        message);

    return ResponseEntity.badRequest().body(body);
  }

  // 2) Bean Validation for @PathVariable, @RequestParam, etc.
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
    String message = ex.getConstraintViolations().stream()
        .map(v -> v.getPropertyPath() + " " + v.getMessage())
        .collect(Collectors.joining("; "));

    ApiError body = new ApiError(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        message);

    return ResponseEntity.badRequest().body(body);
  }
}
