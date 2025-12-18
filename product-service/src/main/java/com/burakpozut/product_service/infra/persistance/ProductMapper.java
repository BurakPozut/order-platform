package com.burakpozut.product_service.infra.persistance;

import com.burakpozut.product_service.domain.Product;

public class ProductMapper {
  public static Product toDomain(ProductJpaEntity entity) {
    return Product.rehydrate(entity.getId(), entity.getName(),
        entity.getPrice(),
        entity.getCurrency(),
        entity.getStatus(),
        entity.getVersion(),
        entity.getInventory());
  }

  public static ProductJpaEntity toEntity(Product p, boolean isNew) {
    var entity = new ProductJpaEntity();
    entity.setId(p.id());
    entity.setName(p.name());
    entity.setPrice(p.price());
    entity.setCurrency(p.currency());
    entity.setStatus(p.status());
    entity.setNew(isNew);
    return entity;
  }
}
