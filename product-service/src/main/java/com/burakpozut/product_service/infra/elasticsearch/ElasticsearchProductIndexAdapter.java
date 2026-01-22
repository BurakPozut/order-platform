package com.burakpozut.product_service.infra.elasticsearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.burakpozut.product_service.app.port.ProductSearchIndex;
import com.burakpozut.product_service.domain.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchProductIndexAdapter implements ProductSearchIndex {

    private final ProductElasticsearchRepository elasticsearchRepository;

    @Override
    public void indexAll(List<Product> products) {
        if (products == null || products.isEmpty()) {
            log.debug("No products to index");
            return;
        }

        try {
            List<ProductDocument> documents = products.stream()
                    .map(ProductDocument::from)
                    .collect(Collectors.toList());

            elasticsearchRepository.saveAll(documents);
            log.info("Indexed {} products to Elasticsearch", documents.size());
        } catch (Exception e) {
            log.error("Failed to index products to Elasticsearch", e);
            throw new RuntimeException("Failed to index products", e);
        }
    }
}