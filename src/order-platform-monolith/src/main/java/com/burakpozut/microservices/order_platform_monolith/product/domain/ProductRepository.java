package com.burakpozut.microservices.order_platform_monolith.product.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository {

  Optional<Product> findById(UUID id);

  Optional<Product> findByName(String name);

  List<Product> findAll();

  Set<Product> findAllByIds(Set<UUID> productIds);

  Product save(Product product, boolean isNew);
}
