package com.burakpozut.microservices.order_platform.product.application.exception;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {
  public ProductNotFoundException(UUID id) {
    super("Product not found with id: " + id);
  }

  public ProductNotFoundException(String name) {
    super("Product not foun with name: " + name);
  }
}
