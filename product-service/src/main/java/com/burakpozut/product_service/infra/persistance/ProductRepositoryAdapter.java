package com.burakpozut.product_service.infra.persistance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {
  private final SpringDataProductRepository jpa;

  @Override
  public List<Product> findAll() {
    return jpa.findAll().stream().map(ProductMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return jpa.findById(id).map(ProductMapper::toDomain);
  }

  @Override
  public Optional<Product> findByName(String name) {
    return jpa.findByName(name).map(ProductMapper::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public Product save(Product product, boolean isNew) {
    var entity = ProductMapper.toEntity(product, isNew);
    var savedEntity = jpa.save(entity);
    return ProductMapper.toDomain(savedEntity);
  }

}
