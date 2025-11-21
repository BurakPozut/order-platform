package com.burakpozut.microservices.order_platform_monolith.order.infrastructure.product;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.order.domain.port.ProductGateway;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductGatewayAdapter implements ProductGateway {
  private final ProductRepository productRepository;

  @Override
  public Optional<Product> geProductById(UUID productId) {
    return productRepository.findById(productId);
  }

  @Override
  public Map<UUID, Product> getProductsById(Set<UUID> productIds) {
    var products = productRepository.findAllByIds(productIds);

    return products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
  }

}
