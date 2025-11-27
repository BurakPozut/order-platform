package com.burakpozut.customer_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class EmailAlreadyInUseException extends BusinessException {

  public EmailAlreadyInUseException(String email) {
    super("Email is already in use: " + email);
  }
}
