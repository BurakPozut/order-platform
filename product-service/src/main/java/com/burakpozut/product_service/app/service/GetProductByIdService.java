package com.burakpozut.product_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductByIdService {
  private final ProductRepository productRepository;

  public Product handle(UUID id){
    log.debug("product.getById.start productId={}", id);
    var product = productRepository.findById(id).orElseThrow(
      ()-> new ProductNotFoundException(id)
    );
    log.info("product.getById.found productId={} name={} status={} inventory={}",
        product.id(), product.name(), product.status(), product.inventory());
    return product;
  }
  
}
