package com.burakpozut.microservices.order_platform.product.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.BusinessException;

public class ProductNotAvailbaleException extends BusinessException {

  public ProductNotAvailbaleException(UUID id) {
    super("Product is not available for ordering. id: " + id);
  }
}
