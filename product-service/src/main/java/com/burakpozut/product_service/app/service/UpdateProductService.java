package com.burakpozut.product_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.app.command.UpdateProductCommand;
import com.burakpozut.product_service.app.exception.NameAlreadyInUseException;
import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateProductService {
  private final ProductRepository productRepository;

  public Product handle(UUID id, UpdateProductCommand command) {
    var existing = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    if (existing.name().equals(command.name()) && productRepository.existsByName(command.name())) {
      throw new NameAlreadyInUseException(command.name());
    }

    var updated = Product.rehydrate(id, command.name(), command.price(), command.currency(), command.status());
    return productRepository.save(updated, false);
  }

}
