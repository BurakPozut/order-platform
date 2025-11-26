package com.burakpozut.microservices.order_platform.product.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductByNameService {
  private final ProductRepository productRepository;

  public Product handle(String name) {
    return productRepository.findByName(name).orElseThrow(() -> new ProductNotFoundException(name));
  }
}
