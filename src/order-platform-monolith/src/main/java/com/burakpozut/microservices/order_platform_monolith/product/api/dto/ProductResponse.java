package com.burakpozut.microservices.order_platform_monolith.product.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

public record ProductResponse(
    UUID id,
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status) {
}
