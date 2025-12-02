package com.burakpozut.product_service.api.dto.request;

import java.math.BigDecimal;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
    @NotBlank String name,
    @Positive BigDecimal price,
    @NotNull Currency currency,
    @NotNull ProductStatus status) {

}
