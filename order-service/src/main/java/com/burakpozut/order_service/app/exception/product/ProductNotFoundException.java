package com.burakpozut.order_service.app.exception.product;

import java.util.UUID;

import com.burakpozut.common.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
  public ProductNotFoundException(UUID id) {
    super("Product not found with id: " + id);
  }

}
