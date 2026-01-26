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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatchProductService {
  private final ProductRepository productRepository;

  public Product handle(UUID id, PatchProductCommand command) {
    log.info("product.patch.start productId={}", id);

    var existing = productRepository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    String newName = command.name() != null ? command.name() : existing.name();
    BigDecimal price = command.price() != null ? command.price() : existing.price();
    Currency currency = command.currency() != null ? command.currency() : existing.currency();
    ProductStatus status = command.status() != null ? command.status() : existing.status();

    if (command.name() != null && !existing.name().equals(newName)
        && productRepository.existsByName(newName)) {
      log.warn("product.patch.name_conflict productId={} newName={}",
          id, newName);
      throw new NameAlreadyInUseException(newName);
    }

    var updated = Product.rehydrate(id, newName, price, currency, status,
        existing.version(),
        existing.inventory());
    var saved = productRepository.save(updated, false);
    log.info("product.patch.completed productId={} name={} status={}",
        saved.id(), saved.name(), saved.status());
    return saved;
  }

}
