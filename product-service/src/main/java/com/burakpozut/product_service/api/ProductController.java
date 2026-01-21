package com.burakpozut.product_service.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.product_service.api.dto.request.PatchProductRequest;
import com.burakpozut.product_service.api.dto.request.ReserveInventoryRequest;
import com.burakpozut.product_service.api.dto.request.UpdateProductRequest;
import com.burakpozut.product_service.api.dto.response.ProductResponse;
import com.burakpozut.product_service.app.service.DeleteProductService;
import com.burakpozut.product_service.app.service.GetAllProductsService;
import com.burakpozut.product_service.app.service.GetProductByIdService;
import com.burakpozut.product_service.app.service.GetProductByNameService;
import com.burakpozut.product_service.app.service.PatchProductService;
import com.burakpozut.product_service.app.service.RerserveInventoryService;
import com.burakpozut.product_service.app.service.UpdateProductService;
import com.burakpozut.product_service.app.service.elasticsearch.ProductSearchQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final GetAllProductsService getAllProducts;
    private final GetProductByIdService getProductByIdService;
    private final GetProductByNameService getProductByNameServie;
    private final UpdateProductService updateProductService;
    private final PatchProductService patchProductService;
    private final DeleteProductService deleteProductService;
    private final RerserveInventoryService rerserveInventoryService;
    private final ProductSearchQueryService productSearchQueryService;

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

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam(required = false) String q) {
        var results = productSearchQueryService.search(q);
        return ResponseEntity.ok(results.stream().map(ProductResponse::from).toList());
    }

    @GetMapping("/search-all")
    public ResponseEntity<List<ProductResponse>> searchAll() {
        var results = productSearchQueryService.search(null);
        return ResponseEntity.ok(results.stream().map(ProductResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        var command = ProductMapper.toCommand(request);
        var product = updateProductService.handle(id, command);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patch(@PathVariable UUID id,
            @Valid @RequestBody PatchProductRequest request) {
        var command = ProductMapper.toCommand(request);
        var customer = patchProductService.handle(id, command);
        return ResponseEntity.ok(ProductResponse.from(customer));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id,
            @RequestBody @Valid ReserveInventoryRequest body) {
        var command = ProductMapper.toCommand(id, body);
        rerserveInventoryService.handle(command);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteProductService.handle(id);
        return ResponseEntity.noContent().build();

    }
}
