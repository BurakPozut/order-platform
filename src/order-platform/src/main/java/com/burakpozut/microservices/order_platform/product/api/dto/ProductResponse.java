package com.burakpozut.microservices.order_platform.product.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.microservices.order_platform.common.domain.Currency;
import com.burakpozut.microservices.order_platform.product.domain.Product;
import com.burakpozut.microservices.order_platform.product.domain.ProductStatus;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        Currency currency,
        ProductStatus status) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getCurrency(), p.getStatus());
    }
}
