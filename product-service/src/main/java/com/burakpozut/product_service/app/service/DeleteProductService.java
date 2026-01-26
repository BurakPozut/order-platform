package com.burakpozut.product_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteProductService {
  private final ProductRepository productRepository;

  public void handle(UUID id) {
    log.info("product.delete.start productId={}", id);
    productRepository.deleteById(id);
    log.info("product.delete.completed productId={}", id);
  }

}
