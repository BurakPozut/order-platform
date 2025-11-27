package com.burakpozut.common.exception;

public abstract class NotFoundException extends AppException {

  public NotFoundException(String message) {
    super(message);
  }
}
