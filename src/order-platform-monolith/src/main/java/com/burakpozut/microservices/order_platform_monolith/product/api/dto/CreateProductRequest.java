package com.burakpozut.microservices.order_platform_monolith.product.api.dto;

import java.math.BigDecimal;

import com.burakpozut.microservices.order_platform_monolith.common.domain.Currency;
import com.burakpozut.microservices.order_platform_monolith.product.domain.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull Currency currency,
        @NotNull ProductStatus status) {

}
