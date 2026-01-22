package com.burakpozut.product_service.app.port;

import java.util.List;

import com.burakpozut.product_service.api.dto.request.SearchRequest;

public interface ProductSearchQuery {

    List<ProductSearchResult> search(String query);

    List<ProductSearchResult> searchWithFilters(SearchRequest request);

    List<String> getSuggestions(String prefix, int limit);

}
