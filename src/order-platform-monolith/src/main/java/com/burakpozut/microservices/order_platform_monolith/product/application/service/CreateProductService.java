package com.burakpozut.microservices.order_platform_monolith.product.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform_monolith.customer.application.command.CreateProductCommand;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateProductService {
  private final ProductRepository productRepository;

  @Transactional
  public Product hande(CreateProductCommand command) {
    var product = Product.createNew(command.getName(), command.getPrice(), command.getCurrency(), command.getStatus());
    return productRepository.save(product, true);
  }

}
