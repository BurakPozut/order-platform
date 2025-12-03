package com.burakpozut.order_service.app.exception.product;

import com.burakpozut.common.exception.AppException;

public class ProductServiceException extends AppException {
  public ProductServiceException(String message) {
    super(message);
  }

  public ProductServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
