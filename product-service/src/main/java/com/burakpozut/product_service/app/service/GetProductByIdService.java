package com.burakpozut.product_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetProductByIdService {
  private final ProductRepository productRepository;

  public Product handle(UUID id){
    return productRepository.findById(id).orElseThrow(
      ()-> new ProductNotFoundException(id)
    );
  }
  
}
