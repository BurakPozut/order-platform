package com.burakpozut.microservices.order_platform.product.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.product.application.exception.ProductNotFoundException;
import com.burakpozut.microservices.order_platform.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteProductService {
  private final ProductRepository productRepository;

  @Transactional
  public void handle(UUID id) {
    if (!productRepository.findById(id).isPresent())
      throw new ProductNotFoundException(id);
    productRepository.deleteById(id);
  }

}
