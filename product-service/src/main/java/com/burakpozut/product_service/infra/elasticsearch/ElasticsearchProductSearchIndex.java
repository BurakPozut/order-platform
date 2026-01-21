package com.burakpozut.product_service.infra.elasticsearch;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.burakpozut.product_service.app.port.ProductSearchIndex;
import com.burakpozut.product_service.app.port.ProductSearchQuery;
import com.burakpozut.product_service.app.port.ProductSearchResult;
import com.burakpozut.product_service.domain.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchProductSearchIndex implements ProductSearchIndex, ProductSearchQuery {

    private static final String INDEX = "products";
    private static final String MAPPING = """
            {"mappings":{"properties":{"name":{"type":"text"},"price":{"type":"double"},"currency":{"type":"keyword"},"status":{"type":"keyword"}}}}
            """;

    private final ObjectMapper objectMapper;
    private final ElasticsearchHttpClient client;
    private final ProductSearchResponseParser responseParser;

    @Override
    public void indexAll(List<Product> products) {
        if (products == null || products.isEmpty())
            return;
        ensureIndex();
        String bulk = buildBulkNdjson(products);
        postBulk(bulk);
        refresh();
    }

    @Override
    public List<ProductSearchResult> search(String query) {
        try {
            String body = buildSearchBody(query);
            ElasticsearchHttpClient.EsResponse r = client.post(INDEX + "/_search", body, "application/json");
            if (r.statusCode() >= 300) {
                log.warn("Elasticsearch search failed: {} - {}", r.statusCode(), r.body());
                return List.of();
            }
            return responseParser.parse(r.body());
        } catch (Exception e) {
            log.warn("Elasticsearch search failed: {}. Returning empty results.", e.getMessage());
            return List.of();
        }
    }

    private void ensureIndex() {
        try {
            ElasticsearchHttpClient.EsResponse r = client.put(INDEX, MAPPING);
            if (r.statusCode() == 400 && r.body().contains("resource_already_exists")) {
                log.debug("Index '{}' already exists.", INDEX);
                return;
            }
            if (r.statusCode() >= 300)
                throw new RuntimeException("ES index create: " + r.statusCode() + " " + r.body());
            log.info("Created index '{}'", INDEX);
        } catch (Exception e) {
            if (isResourceAlreadyExists(e)) {
                log.debug("Index '{}' already exists.", INDEX);
                return;
            }
            throw new RuntimeException("Failed to ensure index " + INDEX, e);
        }
    }

    private String buildBulkNdjson(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Product p : products) {
                sb.append(objectMapper
                        .writeValueAsString(Map.of("index", Map.of("_index", INDEX, "_id", p.id().toString()))))
                        .append('\n');
                sb.append(objectMapper.writeValueAsString(Map.of("name", p.name(), "price", p.price().doubleValue(),
                        "currency", p.currency().name(), "status", p.status().name()))).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Build bulk NDJSON failed", e);
        }
    }

    private String buildSearchBody(String query) throws Exception {
        if (query == null || query.isBlank())
            return objectMapper.writeValueAsString(Map.of("query", Map.of("match_all", Map.of())));
        return objectMapper.writeValueAsString(Map.of("query", Map.of("match", Map.of("name", query))));
    }

    private void postBulk(String bulkBody) {
        ElasticsearchHttpClient.EsResponse r = client.post("_bulk", bulkBody, "application/x-ndjson");
        if (r.statusCode() >= 300)
            throw new RuntimeException("ES bulk: " + r.statusCode() + " " + r.body());
        if (r.body().contains("\"errors\":true"))
            log.warn("Bulk reported errors: {}", r.body());
    }

    private void refresh() {
        try {
            ElasticsearchHttpClient.EsResponse r = client.post(INDEX + "/_refresh");
            if (r.statusCode() >= 300)
                log.debug("Refresh: {} {}", r.statusCode(), r.body());
        } catch (Exception e) {
            log.debug("Refresh failed: {}", e.getMessage());
        }
    }

    private boolean isResourceAlreadyExists(Exception e) {
        for (Throwable t = e; t != null; t = t.getCause())
            if (t.getMessage() != null && t.getMessage().contains("resource_already_exists"))
                return true;
        return false;
    }
}