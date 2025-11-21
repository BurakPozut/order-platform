package com.burakpozut.microservices.order_platform_monolith.product.application.command;

import java.math.BigDecimal;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

public record CreateProductCommand(
    String name,
    BigDecimal price,
    Currency currency,
    ProductStatus status) {
}
