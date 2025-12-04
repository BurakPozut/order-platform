package com.burakpozut.payment_service.app.exception;

import com.burakpozut.common.exception.AppException;

public class OrderServiceException extends AppException {
  public OrderServiceException(String message) {
    super(message);
  }

}
