package com.burakpozut.product_service.app.service;

import com.burakpozut.product_service.app.command.ReleaseInventoryCommand;
import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReleaseInventoryService {
  private final ProductRepository productRepository;

  @Transactional
  public void handle(ReleaseInventoryCommand command) {
    log.info("product.inventory.release.start productId={} quantity={}",
        command.productId(), command.quantity());

    var product = productRepository.findById(command.productId())
        .orElseThrow(() -> new ProductNotFoundException(command.productId()));

    var updated = Product.rehydrate(
        product.id(),
        product.name(),
        product.price(),
        product.currency(),
        product.status(),
        product.version(),
        product.inventory() + command.quantity());

    productRepository.save(updated, false);
    log.info("product.inventory.release.completed productId={} oldInventory={} newInventory={}",
        command.productId(), product.inventory(), updated.inventory());

  }
}
