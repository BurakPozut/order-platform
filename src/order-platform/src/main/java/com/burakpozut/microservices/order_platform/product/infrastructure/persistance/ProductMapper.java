package com.burakpozut.microservices.order_platform.product.infrastructure.persistance;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductStatus;

public class ProductMapper {
  static Product toDomain(ProductJpaEntity entity) {
    return Product.rehydrate(entity.getId(), entity.getName(), entity.getPrice(),
        Currency.valueOf(entity.getCurrency()),
        ProductStatus.valueOf(entity.getStatus()));
  }

  static ProductJpaEntity toEntity(Product p, boolean isNew) {
    var entity = new ProductJpaEntity();
    entity.setId(p.getId());
    entity.setName(p.getName());
    entity.setPrice(p.getPrice());
    entity.setCurrency(p.getCurrency().name());
    entity.setStatus(p.getStatus().name());
    return entity;
  }

}
