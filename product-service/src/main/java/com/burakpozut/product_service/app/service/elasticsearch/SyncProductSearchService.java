package com.burakpozut.product_service.app.service.elasticsearch;

import java.util.List;

// import com.burakpozut.product_service.app.port.ProductSearchIndex;
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
    // private final ProductSearchIndex productSearchIndex;

    public void sync() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            log.info("No products to sync to serch index.");
            return;
        }
        // productSearchIndex.indexAll(products);
        // log.info("Synced {} products to search index.", products.size());
        return;
    }

}
