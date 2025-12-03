package com.burakpozut.order_service.app.exception.customer;

import com.burakpozut.common.exception.AppException;

public class CustomerServiceException extends AppException {

  public CustomerServiceException(String message) {
    super(message);
  }

  public CustomerServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
