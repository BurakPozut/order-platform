package com.burakpozut.product_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.product_service.domain.Product;
import com.burakpozut.product_service.domain.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllProductsService {
    private final ProductRepository productRepository;

    public List<Product> handle() {
        log.info("product.getAll.start");
        var products = productRepository.findAll();
        log.info("product.getAll.completed count={}", products.size());
        return products;
    }

}
