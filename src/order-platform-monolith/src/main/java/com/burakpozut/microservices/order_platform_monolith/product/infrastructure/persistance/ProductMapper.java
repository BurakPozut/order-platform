package com.burakpozut.microservices.order_platform_monolith.product.infrastructure.persistance;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.product.domain.Product;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

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
