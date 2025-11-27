package com.burakpozut.customer_service.config;

import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.burakpozut.common.api.ApiError;
import com.burakpozut.common.exception.AppException;
import com.burakpozut.common.exception.BusinessException;
import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.NotFoundException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
    var body = ApiError.of(HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.name(),
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiError> handleBusinessException(BusinessException ex) {
    ApiError body = ApiError.of(
        HttpStatus.CONFLICT.value(),
        HttpStatus.CONFLICT.name(),
        ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ApiError> handleDomainValidation(DomainValidationException ex) {
    ApiError body = ApiError.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.name(),
        ex.getMessage());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiError> handleAppException(AppException ex) {
    ApiError body = ApiError.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.name(),
        ex.getMessage());

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ApiError> handleDatabaseError(DataAccessException ex) {
    ApiError body = ApiError.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.name(),
        "A database error occurred");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnkown(Exception ex) {
    ApiError body = ApiError.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.name(),
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

    ApiError body = ApiError.of(
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

    ApiError body = ApiError.of(
        HttpStatus.BAD_REQUEST.value(),
        "VALIDATION_ERROR",
        message);

    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    String message = ex.getMessage();

    var body = ApiError.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.name(),
        message);
    return ResponseEntity.badRequest().body(body);
  }
}
