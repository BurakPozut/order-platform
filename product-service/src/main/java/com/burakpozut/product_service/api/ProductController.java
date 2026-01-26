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
import com.burakpozut.product_service.api.dto.request.SearchRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@Slf4j
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
        log.info("api.product.getAll.start");
        var products = getAllProducts.handle();
        var response = products.stream().map(ProductResponse::from).collect(Collectors.toList());
        log.info("api.product.getAll.completed count={}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        log.info("api.product.getById.start productId={}", id);
        var product = getProductByIdService.handle(id);
        var response = ProductResponse.from(product);
        log.info("api.product.getById.completed productId={}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProductResponse> getByName(@PathVariable String name) {
        log.info("api.product.getByName.start name={}", name);
        var product = getProductByNameServie.handle(name);
        var response = ProductResponse.from(product);
        log.info("api.product.getByName.completed name={} productId={}", name, product.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "ACTIVE") List<String> status,
            @RequestParam(required = false, defaultValue = "USD") String currency,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("api.product.search.start query={} status={} currency={} minPrice={} maxPrice={} page={} size={}",
                q, status, currency, minPrice, maxPrice, page, size);

        SearchRequest request = new SearchRequest();
        request.setQuery(q != null && !q.isEmpty() ? q : null);
        request.setStatuses(status);
        request.setCurrency(currency);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setSortBy(sortBy);
        request.setSortOrder(sortOrder);
        request.setPage(page);
        request.setSize(size);

        var results = productSearchQueryService.searchWithFilters(request);
        var response = results.stream()
                .map(ProductResponse::from)
                .toList();
        log.info("api.product.search.completed resultCount={}", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> getSuggestions(
            @RequestParam(required = false, defaultValue = "") String prefix,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("api.product.search.suggestions.start prefix={} limit={}", prefix, limit);
        var suggestions = productSearchQueryService.getSuggestions(
                prefix != null && !prefix.isEmpty() ? prefix : "", limit);
        log.info("api.product.search.suggestions.completed count={}", suggestions.size());
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search-all")
    public ResponseEntity<List<ProductResponse>> searchAll() {
        log.info("api.product.searchAll.start");
        var results = productSearchQueryService.search(null);
        var response = results.stream().map(ProductResponse::from).toList();
        log.info("api.product.searchAll.completed count={}", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("api.product.update.start productId={} name={} price={} currency={} status={}",
                id, request.name(), request.price(), request.currency(), request.status());
        var command = ProductMapper.toCommand(request);
        var product = updateProductService.handle(id, command);
        log.info("api.product.update.completed productId={}", id);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patch(@PathVariable UUID id,
            @Valid @RequestBody PatchProductRequest request) {
        log.info("api.product.patch.start productId={} status={}", id, request.status());
        var command = ProductMapper.toCommand(request);
        var product = patchProductService.handle(id, command);
        log.info("api.product.patch.completed productId={}", id);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id,
            @RequestBody @Valid ReserveInventoryRequest body) {
        log.info("api.product.reserve.start productId={} quantity={}", id, body.quantity());
        var command = ProductMapper.toCommand(id, body);
        rerserveInventoryService.handle(command);
        log.info("api.product.reserve.completed productId={}", id);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("api.product.delete.start productId={}", id);
        deleteProductService.handle(id);
        log.info("api.product.delete.completed productId={}", id);
        return ResponseEntity.noContent().build();

    }
}
