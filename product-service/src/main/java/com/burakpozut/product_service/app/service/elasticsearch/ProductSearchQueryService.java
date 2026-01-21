package com.burakpozut.product_service.app.service.elasticsearch;

import java.util.List;

import com.burakpozut.product_service.app.port.ProductSearchQuery;
import com.burakpozut.product_service.app.port.ProductSearchResult;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchQueryService {
    private final ProductSearchQuery productSearchQuery;

    public List<ProductSearchResult> search(String query) {
        return productSearchQuery.search(query);
    }

}
