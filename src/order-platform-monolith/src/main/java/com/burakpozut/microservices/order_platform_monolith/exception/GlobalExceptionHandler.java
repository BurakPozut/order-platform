package com.burakpozut.microservices.order_platform_monolith.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.burakpozut.microservices.order_platform_monolith.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomerNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .message(ex.getMessage()).error("Customer not found").build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}
