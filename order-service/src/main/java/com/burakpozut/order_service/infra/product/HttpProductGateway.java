package com.burakpozut.order_service.infra.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HttpProductGateway implements ProductGateway {

  private final WebClient webClient;

  private record ProductResponse(UUID id, String name,
      BigDecimal price, Currency currency) {
  }

  public HttpProductGateway(WebClient.Builder builder,
      @Value("${product.service.url}") String productServiceUrl) {
    this.webClient = builder.baseUrl(productServiceUrl).build();
  }

  @Override
  public Optional<ProductInfo> getProductById(UUID productId) {
    try {
      ProductResponse response = webClient.get()
          .uri("/api/products/{id}", productId)
          .retrieve()
          .bodyToMono(ProductResponse.class)
          .block();

      return Optional.of(new ProductInfo(response.id, response.name, response.price, response.currency));
    } catch (WebClientResponseException.NotFound e) {
      return Optional.empty();
    } catch (WebClientResponseException e) {
      log.error("Product service error for product {}: {} - {}",
          productId, e.getStatusCode(), e.getMessage());
      throw new ExternalServiceException("Product service returned error: " + e.getStatusCode(), e);
    } catch (Exception e) {
      log.error("Failed to communicate with product service for product {}: {}",
          productId, e.getMessage());
      throw new ExternalServiceException("Product service is unavailable", e);
    }
  }

  @Override
  public Map<UUID, ProductInfo> getProductsByIds(List<UUID> productIds) {
    if (productIds.isEmpty()) {
      return Map.of();
    }

    List<Mono<ProductInfo>> productMonos = productIds.stream()
        .map(productId -> webClient.get()
            .uri("/api/products/{id}", productId)
            .retrieve()
            .bodyToMono(ProductResponse.class)
            .map(r -> new ProductInfo(r.id, r.name, r.price, r.currency))
            .onErrorResume(WebClientResponseException.NotFound.class,
                e -> {
                  log.warn("Proudct not found {}", productId);
                  return Mono.empty();
                })
            .onErrorMap(WebClientResponseException.class, e -> {
              // 500 error and 400 error except 404
              log.error("Product service error for product {}: {} - {}",
                  productId, e.getStatusCode(), e.getMessage());
              return new ExternalServiceException("Product service returned error: " + e.getStatusCode(), e);
            })
            .onErrorMap(Exception.class, e -> {
              // network errors, timeouts, connection refused...
              log.error("Failed to communicate with product service for product {}: {}",
                  productId, e.getMessage());
              return new ExternalServiceException("Product service is unavailable", e);
            }))
        .toList();

    try {
      List<ProductInfo> products = Flux.merge(productMonos).collectList().block();

      return products.stream().collect(Collectors.toMap(ProductInfo::productId, Function.identity()));
    } catch (Exception e) {
      log.error("Unexpecter error fetching products", e);
      throw new ExternalServiceException("Failed to fetch products", e);
    }
  }

}
