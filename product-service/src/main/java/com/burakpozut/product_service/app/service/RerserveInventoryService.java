package com.burakpozut.product_service.app.service;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.app.command.ReserveInventoryCommand;
import com.burakpozut.product_service.app.exception.InsufficentInventoryException;
import com.burakpozut.product_service.app.exception.ProductNotFoundException;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RerserveInventoryService {
  private final ProductRepository productRepository;

  @Transactional
  public void handle(ReserveInventoryCommand command) {
    var product = productRepository.findById(command.productId())
        .orElseThrow(() -> new ProductNotFoundException(command.productId()));

    if (product.inventory() < command.quantity()) {
      throw new InsufficentInventoryException(command.productId());
    }

    // This is a pretty simple inventory reserver logic it is usually best to handle
    // this with a one single sql query for simplicty
    var updated = Product.rehydrate(product.id(),
        product.name(),
        product.price(),
        product.currency(),
        product.status(),
        product.version(),
        product.inventory() - command.quantity());

    productRepository.save(updated, false);

  }

}
