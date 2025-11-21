package com.burakpozut.microservices.order_platform.product.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.burakpozut.microservices.order_platform.product.application.command.CreateProductCommand;
import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateProductService {
  private final ProductRepository productRepository;

  @Transactional
  public Product hande(CreateProductCommand command) {
    var product = Product.createNew(command.name(), command.price(), command.currency(), command.status());
    return productRepository.save(product, true);
  }

}
