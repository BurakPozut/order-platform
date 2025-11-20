package com.burakpozut.microservices.order_platform_monolith.product.application.service;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductByNameService {
  private final ProductRepository productRepository;

  public Product handle(String name) {
    return productRepository.findByName(name).orElseThrow(() -> new ProductNotFoundException(name));
  }
}
