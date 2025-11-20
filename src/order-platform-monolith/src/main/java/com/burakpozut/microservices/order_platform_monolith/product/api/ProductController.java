package com.burakpozut.microservices.order_platform_monolith.product.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform_monolith.product.api.dto.ProductResponse;
import com.burakpozut.microservices.order_platform_monolith.product.application.service.GetProductByIdService;
import com.burakpozut.microservices.order_platform_monolith.product.application.service.GetProductByNameService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
  private final GetProductByIdService getProductByIdService;
  private final GetProductByNameService getProductByNameService;

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getById(
      @Parameter(description = "Product ID", required = true, schema = @Schema(type = "string", format = "uuid", example = "0989a886-d533-4bcc-97a5-c005562afaae")) @PathVariable UUID id) {
    var product = getProductByIdService.hande(id);
    var response = new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getCurrency(),
        product.getStatus());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<ProductResponse> getByName(@PathVariable String name) {
    var product = getProductByNameService.handle(name);
    var response = new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getCurrency(),
        product.getStatus());
    return ResponseEntity.ok(response);
  }

}
