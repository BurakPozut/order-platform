package com.burakpozut.product_service.app.service.elasticsearch;

import java.util.List;

import com.burakpozut.product_service.app.port.ProductSearchIndex;
import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncProductSearchService {

    private final ProductRepository productRepository;
    private final ProductSearchIndex productSearchIndex;

    public void sync() {
        log.info("product.search.sync.start");
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.info("product.search.sync.empty action=skipping");
            return;
        }
        productSearchIndex.indexAll(products);
        log.info("product.search.sync.completed count={}", products.size());
    }

}