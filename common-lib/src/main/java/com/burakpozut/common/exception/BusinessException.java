package com.burakpozut.common.exception;

public abstract class BusinessException extends AppException {

  public BusinessException(String message) {
    super(message);
  }
}
