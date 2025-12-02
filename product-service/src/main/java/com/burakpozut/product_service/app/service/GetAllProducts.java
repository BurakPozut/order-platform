package com.burakpozut.product_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllProducts {
  private final ProductRepository productRepository;

  public List<Product> handle() {
    return productRepository.findAll();
  }

}
