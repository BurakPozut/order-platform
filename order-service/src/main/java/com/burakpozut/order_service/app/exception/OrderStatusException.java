package com.burakpozut.order_service.app.exception;

import com.burakpozut.common.exception.BusinessException;

public class OrderStatusException extends BusinessException {
  public OrderStatusException(String message) {
    super(message);
  }

}
