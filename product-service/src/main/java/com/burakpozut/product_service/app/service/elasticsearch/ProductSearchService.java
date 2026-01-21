package com.burakpozut.product_service.app.service.elasticsearch;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;

// import com.burakpozut.product_service.domain.Product;
// import com.burakpozut.product_service.domain.ProductRepository;
import com.burakpozut.product_service.infra.elasticsearch.ProductSearchDoc;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    // private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    // public void syncFromDatabase() {
    // var indexOps = elasticsearchOperations.indexOps(ProductSearchDoc.class);
    public String searchAll() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:9200/products/_search"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("""
                                {"query":{"match_all":{}}}
                            """))
                    .build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() >= 300) {
                throw new RuntimeException("ES error " + res.statusCode() + ": " + res.body());
            }
            return res.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductSearchDoc> search(String query) {
        try {
            if (query == null || query.isBlank()) {
                var q = new CriteriaQuery(new Criteria());
                SearchHits<ProductSearchDoc> hits = elasticsearchOperations.search(q, ProductSearchDoc.class);
                return hits.getSearchHits().stream().map(SearchHit::getContent).toList();
            }
            Criteria criteria = new Criteria("name").contains(query);
            CriteriaQuery q = new CriteriaQuery(criteria);
            SearchHits<ProductSearchDoc> hits = elasticsearchOperations.search(q, ProductSearchDoc.class);
            return hits.getSearchHits().stream().map(SearchHit::getContent).toList();
        } catch (DataAccessException e) {
            log.warn("Elasticsearch search failed: {}. Returning empty results.", e.getMessage());
            return List.of();
        }
    }

    // private ProductSearchDoc toDoc(Product p) {
    // return new ProductSearchDoc(
    // p.id().toString(),
    // p.name(),
    // p.price().doubleValue(),
    // p.currency().name(),
    // p.status().name());
    // }

    // private boolean isAlreadyExistsException(Throwable t) {
    // for (Throwable x = t; x != null; x = x.getCause()) {
    // String m = x.getMessage();
    // if (m != null && m.contains("resource_already_exists")) {
    // return true;
    // }
    // }
    // return false;
    // }
}
