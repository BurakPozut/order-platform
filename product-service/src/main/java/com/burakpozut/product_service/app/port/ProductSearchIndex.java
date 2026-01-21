package com.burakpozut.product_service.app.port;

import java.util.List;

import com.burakpozut.product_service.domain.Product;

public interface ProductSearchIndex {
    void indexAll(List<Product> products);
}
