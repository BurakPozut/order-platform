package com.burakpozut.microservices.order_platform.product.application.command;

import java.math.BigDecimal;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.product.domain.ProductStatus;

public record CreateProductCommand(
        String name,
        BigDecimal price,
        Currency currency,
        ProductStatus status) {
}
