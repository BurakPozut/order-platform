package com.burakpozut.product_service.infra.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.burakpozut.product_service.api.dto.request.SearchRequest;
import com.burakpozut.product_service.app.port.ProductSearchQuery;
import com.burakpozut.product_service.app.port.ProductSearchResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchProductQueryAdapter implements ProductSearchQuery {

    private final ProductElasticsearchRepository elasticsearchRepository;

    @Override
    public List<ProductSearchResult> search(String query) {
        try {
            List<ProductDocument> documents;

            if (query == null || query.isBlank()) {
                documents = (List<ProductDocument>) elasticsearchRepository.findAll();
            } else {
                documents = elasticsearchRepository.findByNameContainingIgnoreCase(query);
            }

            return documents.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("elasticsearch.search.failed query={} message={} action=returning_empty",
                    query, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<ProductSearchResult> searchWithFilters(SearchRequest request) {
        try {
            Pageable pageable = createPageable(request);
            Page<ProductDocument> page;

            // Use the most specific repository method that pushes ALL filters to
            // Elasticsearch
            if (request.getQuery() != null && !request.getQuery().isBlank()) {
                // Has search query - use combined method with all filters
                page = searchWithQueryAndFilters(request, pageable);
            } else {
                // No search query - just filters
                page = searchWithFiltersOnly(request, pageable);
            }

            return page.getContent().stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("elasticsearch.search.filters_failed query={} message={} action=returning_empty",
                    request.getQuery(), e.getMessage(), e);
            return List.of();
        }
    }

    private Page<ProductDocument> searchWithQueryAndFilters(SearchRequest request, Pageable pageable) {
        String query = request.getQuery();
        List<String> statuses = request.getStatuses();
        String currency = request.getCurrency();
        Double minPrice = request.getMinPrice();
        Double maxPrice = request.getMaxPrice();

        // Use the most complete method if all filters are provided
        if (statuses != null && !statuses.isEmpty() && currency != null && minPrice != null && maxPrice != null) {
            // ALL filters + query - everything in Elasticsearch!
            return elasticsearchRepository
                    .findByNameContainingIgnoreCaseAndStatusInAndCurrencyAndPriceBetween(
                            query, statuses, currency, minPrice, maxPrice, pageable);
        } else if (statuses != null && !statuses.isEmpty() && minPrice != null && maxPrice != null) {
            // Query + Status + Price (no currency filter)
            String status = statuses.get(0); // Use first status
            return elasticsearchRepository
                    .findByNameContainingIgnoreCaseAndStatusAndPriceBetween(
                            query, status, minPrice, maxPrice, pageable);
        } else {
            // Partial filters - get all matching query, then filter in memory (fallback)
            // TODO: Add more repository methods to handle all combinations
            List<ProductDocument> all = elasticsearchRepository.findByNameContainingIgnoreCase(query);
            return applyFiltersAndPaginationInMemory(all, request, pageable);
        }
    }

    private Page<ProductDocument> searchWithFiltersOnly(SearchRequest request, Pageable pageable) {
        List<String> statuses = request.getStatuses();
        String currency = request.getCurrency();
        Double minPrice = request.getMinPrice();
        Double maxPrice = request.getMaxPrice();

        if (statuses != null && !statuses.isEmpty() && currency != null && minPrice != null && maxPrice != null) {
            // All filters - everything in Elasticsearch!
            return elasticsearchRepository
                    .findByStatusInAndCurrencyAndPriceBetween(statuses, currency, minPrice, maxPrice, pageable);
        } else if (minPrice != null && maxPrice != null) {
            // Just price range
            List<ProductDocument> all = elasticsearchRepository.findByPriceBetween(minPrice, maxPrice);
            return applyFiltersAndPaginationInMemory(all, request, pageable);
        } else {
            // No filters - return all with pagination
            return elasticsearchRepository.findAll(pageable);
        }
    }

    // Fallback method - only used when repository doesn't have the exact
    // combination
    private Page<ProductDocument> applyFiltersAndPaginationInMemory(
            List<ProductDocument> documents, SearchRequest request, Pageable pageable) {
        // Apply remaining filters in memory (only as fallback)
        List<ProductDocument> filtered = documents.stream()
                .filter(doc -> request.getStatuses() == null ||
                        request.getStatuses().isEmpty() ||
                        request.getStatuses().contains(doc.getStatus()))
                .filter(doc -> request.getCurrency() == null ||
                        request.getCurrency().equals(doc.getCurrency()))
                .filter(doc -> request.getMinPrice() == null ||
                        doc.getPrice() >= request.getMinPrice())
                .filter(doc -> request.getMaxPrice() == null ||
                        doc.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());

        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<ProductDocument> paginated = start < filtered.size()
                ? filtered.subList(start, end)
                : List.of();

        return new org.springframework.data.domain.PageImpl<>(
                paginated, pageable, filtered.size());
    }

    @Override
    public List<String> getSuggestions(String prefix, int limit) {
        try {
            return elasticsearchRepository
                    .findByNameStartingWithIgnoreCase(prefix)
                    .stream()
                    .limit(limit)
                    .map(ProductDocument::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("elasticsearch.suggestions.failed prefix={} limit={} message={} action=returning_empty",
                    prefix, limit, e.getMessage());
            return List.of();
        }
    }

    private Pageable createPageable(SearchRequest request) {
        Sort sort = createSort(request);
        return PageRequest.of(
                request.getPage() != null ? request.getPage() : 0,
                request.getSize() != null ? request.getSize() : 20,
                sort);
    }

    private Sort createSort(SearchRequest request) {
        if (request.getSortBy() == null) {
            return Sort.unsorted();
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, request.getSortBy());
    }

    private ProductSearchResult toSearchResult(ProductDocument document) {
        return new ProductSearchResult(
                java.util.UUID.fromString(document.getId()),
                document.getName(),
                java.math.BigDecimal.valueOf(document.getPrice()),
                com.burakpozut.common.domain.Currency.valueOf(document.getCurrency()),
                com.burakpozut.product_service.domain.ProductStatus.valueOf(document.getStatus()));
    }
}