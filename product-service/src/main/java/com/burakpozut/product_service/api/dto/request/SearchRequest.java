package com.burakpozut.product_service.api.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private List<String> statuses;
    private String currency;
    private Double minPrice;
    private Double maxPrice;
    private List<String> categories;
    private String sortBy; // "price", "name", "createdAt"
    private String sortOrder; // "asc", "desc"
    private Integer page = 0;
    private Integer size = 20;
}
