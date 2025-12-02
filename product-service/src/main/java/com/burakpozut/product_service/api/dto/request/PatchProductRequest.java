package com.burakpozut.product_service.api.dto.request;

import java.math.BigDecimal;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;

import jakarta.annotation.Nullable;

public record PatchProductRequest(
    @Nullable String name,
    @Nullable BigDecimal price,
    @Nullable Currency currency,
    @Nullable ProductStatus status) {

}
