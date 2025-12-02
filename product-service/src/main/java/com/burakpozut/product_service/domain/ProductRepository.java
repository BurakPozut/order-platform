package com.burakpozut.product_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

  Optional<Product> findById(UUID id);

  Optional<Product> findByName(String name);

  List<Product> findAll();

  Product save(Product product, boolean isNew);

  void deleteById(UUID id);

}
