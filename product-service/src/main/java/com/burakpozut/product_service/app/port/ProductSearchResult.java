package com.burakpozut.product_service.app.port;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.domain.ProductStatus;

public record ProductSearchResult(
        UUID id,
        String name,
        BigDecimal price,
        Currency currency,
        ProductStatus status) {

}
