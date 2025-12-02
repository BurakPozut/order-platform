package com.burakpozut.product_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductByNameService {
  private final ProductRepository productRepository;

  public Product handle(String name){
    return productRepository.findByName(name).orElseThrow(
      ()-> new ProductNotFoundException(name)
    );
  }
  
}
