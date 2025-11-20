package com.burakpozut.microservices.order_platform_monolith.product.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform_monolith.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductByIdService {
  private final ProductRepository productRepository;

  public Product hande(UUID id) {
    return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
  }

}
