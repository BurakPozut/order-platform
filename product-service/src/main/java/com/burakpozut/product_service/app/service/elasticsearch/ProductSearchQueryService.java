package com.burakpozut.product_service.app.service.elasticsearch;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.api.dto.request.SearchRequest;
import com.burakpozut.product_service.app.port.ProductSearchQuery;
import com.burakpozut.product_service.app.port.ProductSearchResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchQueryService {
    private final ProductSearchQuery productSearchQuery;

    public List<ProductSearchResult> search(String query) {
        return productSearchQuery.search(query);
    }

    public List<ProductSearchResult> searchWithFilters(SearchRequest request) {
        // Delegate to the port implementation
        return productSearchQuery.searchWithFilters(request);
    }

    public List<String> getSuggestions(String prefix, int limit) {
        return productSearchQuery.getSuggestions(prefix, limit);
    }
}