package com.burakpozut.order_service.infra.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HttpProductGateway implements ProductGateway {

  private final WebClient webClient;
  private final CircuitBreaker circuitBreaker;
  private final Retry retry;

  private record ProductResponse(UUID id, String name,
      BigDecimal price, Currency currency) {
  }

  private record ReserveInventoryRequest(Integer quantity) {
  }

  public HttpProductGateway(WebClient.Builder builder,
      @Value("${product.service.url}") String productServiceUrl,
      CircuitBreaker productCircuitBreaker,
      Retry productRetry) {
    this.webClient = builder.baseUrl(productServiceUrl).build();
    this.circuitBreaker = productCircuitBreaker;
    this.retry = productRetry;
  }

  @Override
  public Optional<ProductInfo> getProductById(UUID productId) {
    // try {
    ProductResponse response = webClient.get()
        .uri("/api/products/{id}", productId)
        .retrieve()
        .bodyToMono(ProductResponse.class)
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorMap(error -> mapError(error, productId))
        .block();

    return Optional.of(new ProductInfo(response.id, response.name, response.price, response.currency));
    // } catch (WebClientResponseException.NotFound e) {
    // return Optional.empty();
    // } catch (WebClientResponseException e) {
    // log.error("Product service error for product {}: {} - {}",
    // productId, e.getStatusCode(), e.getMessage());
    // throw new ExternalServiceException("Product service returned error: " +
    // e.getStatusCode(), e);
    // } catch (Exception e) {
    // log.error("Failed to communicate with product service for product {}: {}",
    // productId, e.getMessage());
    // throw new ExternalServiceException("Product service is unavailable", e);
    // }
  }

  @Override
  public Map<UUID, ProductInfo> getProductsByIds(List<UUID> productIds) {
    if (productIds.isEmpty()) {
      return Map.of();
    }

    List<Mono<ProductInfo>> productMonos = productIds.stream()
        .map(productId -> fetchProductWithResilience(productId))
        .toList();

    try {
      List<ProductInfo> products = Flux.merge(productMonos).collectList().block();
      return products.stream()
          .collect(Collectors.toMap(ProductInfo::productId, Function.identity()));
    } catch (Exception e) {
      log.error("Unexpected error fetching products", e);
      throw new ExternalServiceException("Failed to fetch products", e);
    }
  }

  private Mono<ProductInfo> fetchProductWithResilience(UUID productId) {
    return webClient.get()
        .uri("/api/products/{id}", productId)
        .retrieve()
        .bodyToMono(ProductResponse.class)
        .map(r -> new ProductInfo(r.id, r.name, r.price, r.currency))
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorResume(WebClientResponseException.NotFound.class,
            e -> {
              log.warn("Product not found: {}", productId);
              return Mono.empty(); // Return empty for 404 - product doesn't exist
            })
        .onErrorMap(error -> mapError(error, productId));
  }

  @Override
  public void reserveInventory(UUID productId, Integer quantity) {
    // try {
    webClient.post()
        .uri("/api/products/{productId}/reserve", productId)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(new ReserveInventoryRequest(quantity))
        .retrieve()
        .toBodilessEntity()
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorMap(error -> mapError(error, productId))
        .block();
    // } catch (WebClientResponseException.NotFound e) {
    // log.error("Product not found {}", productId);
    // throw new ExternalServiceException("Product not found: " + productId, e);
    // } catch (WebClientResponseException e) {
    // log.error("Product service error for product {}: {} - {}",
    // productId, e.getStatusCode(), e.getMessage());
    // throw new ExternalServiceException("Product service returned error: " +
    // e.getStatusCode(), e);
    // } catch (Exception e) {
    // log.error("Failed to communicate with product service for product {}: {}",
    // productId, e.getMessage());
    // throw new ExternalServiceException("Product service is unavailable", e);
    // }

  }

  private Throwable mapError(Throwable error, UUID productId) {
    if (error instanceof WebClientResponseException.NotFound e) {
      return mapNotFoundError(e, productId);
    } else if (error instanceof WebClientResponseException e) {
      return e.getStatusCode().is5xxServerError()
          ? mapServerError(e, productId)
          : mapClientError(e, productId);
    } else {
      return mapNetworkError(error, productId);
    }
  }

  private ExternalServiceNotFoundException mapNotFoundError(WebClientResponseException.NotFound e, UUID productId) {
    log.error("Product endpoint not found for product {}: {} - {}",
        productId, e.getStatusCode(), e.getMessage());
    return new ExternalServiceNotFoundException("Product service endpoint not found: " + e.getMessage());
  }

  private ExternalServiceException mapServerError(WebClientResponseException e, UUID productId) {
    log.error("Product service server error for product {}: {} - {}",
        productId, e.getStatusCode(), e.getMessage());
    return new ExternalServiceException("Product service returned server error: " + e.getStatusCode(), e);
  }

  private ExternalServiceException mapClientError(WebClientResponseException e, UUID productId) {
    log.error("Product service client error for product {}: {} - {}",
        productId, e.getStatusCode(), e.getMessage());
    return new ExternalServiceException("Product service returned client error: " + e.getStatusCode(), e);
  }

  private ExternalServiceException mapNetworkError(Throwable error, UUID productId) {
    log.error("Failed to communicate with Product service for product {}: {}",
        productId, error.getMessage());
    return new ExternalServiceException("Product service is unavailable", error);
  }
}
