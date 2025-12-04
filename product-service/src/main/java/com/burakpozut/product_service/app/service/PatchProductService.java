package com.burakpozut.product_service.app.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.app.command.PatchProductCommand;
import com.burakpozut.product_service.app.exception.NameAlreadyInUseException;
import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;
import com.burakpozut.product_service.domain.ProductStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatchProductService {
  private final ProductRepository productRepository;

  public Product handle(UUID id, PatchProductCommand command) {
    var existing = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    String newName = command.name() != null ? command.name() : existing.name();
    BigDecimal price = command.price() != null ? command.price() : existing.price();
    Currency currency = command.currency() != null ? command.currency() : existing.currency();
    ProductStatus status = command.status() != null ? command.status() : existing.status();

    if (command.name() != null && !existing.name().equals(newName)
        && productRepository.existsByName(newName)) {
      throw new NameAlreadyInUseException(newName);
    }

    var updated = Product.rehydrate(id, newName, price, currency, status);
    return productRepository.save(updated, false);
  }

}
