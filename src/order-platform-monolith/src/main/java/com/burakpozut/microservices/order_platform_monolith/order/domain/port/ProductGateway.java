package com.burakpozut.microservices.order_platform_monolith.order.domain.port;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;

public interface ProductGateway {
  Optional<Product> geProductById(UUID productId);

  Map<UUID, Product> getProductsById(Set<UUID> productIds);
}
