package com.burakpozut.product_service.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.product_service.api.dto.response.ProductResponse;
import com.burakpozut.product_service.app.service.GetAllProductsService;
import com.burakpozut.product_service.app.service.GetProductByIdService;
import com.burakpozut.product_service.app.service.GetProductByNameService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
  private final GetAllProductsService getAllProducts;
  private final GetProductByIdService getProductByIdService;
  private final GetProductByNameService getProductByNameServie;

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAll() {
    var products = getAllProducts.handle();
    var response = products.stream().map(ProductResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
    var product = getProductByIdService.handle(id);
    var response = ProductResponse.from(product);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<ProductResponse> getByName(@PathVariable String name) {
    var product = getProductByNameServie.handle(name);
    var response = ProductResponse.from(product);
    return ResponseEntity.ok(response);
  }

}