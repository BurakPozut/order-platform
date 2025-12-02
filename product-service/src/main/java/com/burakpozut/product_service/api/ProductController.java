package com.burakpozut.product_service.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.product_service.api.dto.response.ProductResponse;
import com.burakpozut.product_service.app.service.GetAllProducts;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
  private final GetAllProducts getAllProducts;

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAll() {
    var products = getAllProducts.handle();
    var response = products.stream().map(ProductResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

}