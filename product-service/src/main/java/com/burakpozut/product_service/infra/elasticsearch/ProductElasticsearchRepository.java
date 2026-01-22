package com.burakpozut.product_service.infra.elasticsearch;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    List<ProductDocument> findByStatus(String status);

    List<ProductDocument> findByCurrency(String currency);

    List<ProductDocument> findByPriceBetween(Double minPrice, Double maxPrice);

    List<ProductDocument> findByNameContainingIgnoreCaseAndStatusAndPriceBetween(
            String name, String status, Double minPrice, Double maxPrice);

    List<ProductDocument> findByNameStartingWithIgnoreCase(String prefix);

    List<ProductDocument> findByStatusInAndCurrencyAndPriceBetween(
            List<String> statuses, String currency, Double minPrice, Double maxPrice);

    List<ProductDocument> findByNameContainingIgnoreCaseAndStatusInAndCurrencyAndPriceBetween(
            String name,
            List<String> statuses,
            String currency,
            Double minPrice,
            Double maxPrice);

    // Add Pageable support for pagination in Elasticsearch
    Page<ProductDocument> findByNameContainingIgnoreCaseAndStatusAndPriceBetween(
            String name, String status, Double minPrice, Double maxPrice, Pageable pageable);

    // Add Pageable support
    Page<ProductDocument> findByStatusInAndCurrencyAndPriceBetween(
            List<String> statuses, String currency, Double minPrice, Double maxPrice, Pageable pageable);

    // Add Pageable support - this is the most complete method
    Page<ProductDocument> findByNameContainingIgnoreCaseAndStatusInAndCurrencyAndPriceBetween(
            String name,
            List<String> statuses,
            String currency,
            Double minPrice,
            Double maxPrice,
            Pageable pageable);
}