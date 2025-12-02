package com.burakpozut.product_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteProductService {
  private final ProductRepository productRepository;

  public void handle(UUID id) {
    productRepository.deleteById(id);
  }

}
