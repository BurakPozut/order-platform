package com.burakpozut.microservices.order_platform.product.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllProductsService {
  private final ProductRepository productRepository;

  public List<Product> handle() {
    return productRepository.findAll();
  }

}
