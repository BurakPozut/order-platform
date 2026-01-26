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
    log.info("product.inventory.reserve.start productId={} quantity={}",
        command.productId(), command.quantity());

    var product = productRepository.findById(command.productId())
        .orElseThrow(() -> new ProductNotFoundException(command.productId()));
    log.debug("product.inventory.reserve.current_inventory productId={} currentInventory={}",
        command.productId(), product.inventory());

    if (product.inventory() < command.quantity()) {
      log.warn("product.inventory.reserve.insufficient productId={} requested={} available={}",
          command.productId(), command.quantity(), product.inventory());
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
    log.info("product.inventory.reserve.completed productId={} oldInventory={} newInventory={}",
        command.productId(), product.inventory(), updated.inventory());

  }

}
