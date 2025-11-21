package com.burakpozut.microservices.order_platform_monolith.product.infrastructure.persistance;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositroyAdapter implements ProductRepository {

  private final SpringDataProductRepository jpa;

  @Override
  public Optional<Product> findById(UUID id) {
    return jpa.findById(id).map(ProductMapper::toDomain);
  }

  @Override
  public Optional<Product> findByName(String name) {
    return jpa.findByName(name).map(ProductMapper::toDomain);
  }

  @Override
  public Product save(Product product, boolean isNew) {
    var entity = ProductMapper.toEntity(product, isNew);
    var savedEntity = jpa.save(entity);
    return ProductMapper.toDomain(savedEntity);
  }

  @Override
  public Set<Product> findAllByIds(Set<UUID> productIds) {
    var products = jpa.findAllById(productIds);
    return products.stream().map(ProductMapper::toDomain).collect(Collectors.toSet());
  }

  @Override
  public List<Product> findAll() {
    return jpa.findAll().stream().map(ProductMapper::toDomain).collect(Collectors.toList());
  }
}
