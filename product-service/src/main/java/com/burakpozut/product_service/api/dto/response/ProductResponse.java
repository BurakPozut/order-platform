package com.burakpozut.product_service.api.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.product_service.app.port.ProductSearchResult;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductStatus;
import com.burakpozut.product_service.infra.elasticsearch.ProductSearchDoc;

public record ProductResponse(
        UUID id,
        String name,
        BigDecimal price,
        Currency currency,
        ProductStatus status) {
    public static ProductResponse from(
            Product p) {
        return new ProductResponse(p.id(), p.name(), p.price(), p.currency(), p.status());
    }

    public static ProductResponse fromSearchDoc(ProductSearchDoc doc) {
        return new ProductResponse(
                UUID.fromString(doc.getId()),
                doc.getName(),
                BigDecimal.valueOf(doc.getPrice()),
                Currency.valueOf(doc.getCurrency()),
                ProductStatus.valueOf(doc.getStatus()));
    }

    public static ProductResponse from(ProductSearchResult r) {
        return new ProductResponse(r.id(), r.name(), r.price(), r.currency(), r.status());
    }
}
