package com.burakpozut.order_service.infra.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.ProductInfo;
import com.burakpozut.order_service.domain.gateway.ProductGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpProductGateway implements ProductGateway {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    private record ProductResponse(UUID id, String name,
            BigDecimal price, Currency currency) {
    }

    private record ReserveInventoryRequest(Integer quantity) {
    }

    public HttpProductGateway(RestClient.Builder builder,
            @Value("${product.service.url}") String productServiceUrl,
            CircuitBreaker productCircuitBreaker,
            Retry productRetry) {
        this.restClient = builder.baseUrl(productServiceUrl).build();
        this.circuitBreaker = productCircuitBreaker;
        this.retry = productRetry;
    }

    @Override
    public Optional<ProductInfo> getProductById(UUID productId) {
        try {
            ProductResponse response = retry.executeSupplier(() -> circuitBreaker.executeSupplier(() -> restClient.get()
                    .uri("/api/products/{id}", productId)
                    .header("X-Source-Service", "order-service")
                    .header("X-Trace-Id", safeTraceId())
                    .retrieve()
                    .body(ProductResponse.class)));
            return Optional.of(new ProductInfo(response.id, response.name, response.price, response.currency));
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                log.warn("product.fetch.not_found productId={}", productId);
                return Optional.empty();
            }
            throw mapResponseError(e, productId);
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(productId);
            return Optional.empty(); // unreachable
        } catch (Exception e) {
            throw mapNetworkError(e, productId);
        }
    }

    @Override
    public Map<UUID, ProductInfo> getProductsByIds(List<UUID> productIds) {
        if (productIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<ProductInfo> products = productIds.stream()
                    .map(this::getProductById)
                    .flatMap(Optional::stream)
                    .toList();

            return products.stream()
                    .collect(Collectors.toMap(ProductInfo::productId, Function.identity()));
        } catch (Exception e) {
            log.error("product.fetch.failed productIds={}", productIds, e);
            throw new ExternalServiceException("Failed to fetch products", e);
        }
    }

    @Override
    public void reserveInventory(UUID productId, Integer quantity) {
        try {
            retry.executeRunnable(() -> circuitBreaker.executeRunnable(() -> {
                restClient.post()
                        .uri("/api/products/{productId}/reserve", productId)
                        .header("X-Source-Service", "order-service")
                        .header("X-Trace-Id", safeTraceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ReserveInventoryRequest(quantity))
                        .retrieve()
                        .toBodilessEntity();
            }));
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(productId);
        } catch (RestClientResponseException e) {
            throw mapResponseError(e, productId);
        } catch (Exception e) {
            throw mapNetworkError(e, productId);
        }
    }

    private String safeTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }

    private void handleCircuitBreakerOpenFallback(UUID productId) {
        log.warn("product.service.circuit_breaker_open productId={} action=skip_request",
                productId);
        throw new ExternalServiceException(
                "Product service circuit breaker is open - cannot process request: " + productId);
    }

    private RuntimeException mapResponseError(RestClientResponseException e, UUID productId) {
        if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().value() == 404) {
                log.error("product.endpoint.not_found productId={} statusCode={} message={}",
                        productId, e.getStatusCode(), e.getMessage());
                return new ExternalServiceNotFoundException("Product service endpoint not found: " + e.getMessage());
            }
            log.error("product.service.client_error productId={} statusCode={} message={}",
                    productId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceException("Product service returned client error: " + e.getStatusCode(), e);
        }

        log.error("product.service.server_error productId={} statusCode={} message={}",
                productId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Product service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID productId) {
        log.error("product.service.communication_failed productId={} message={}",
                productId, error.getMessage());
        return new ExternalServiceException("Product service is unavailable", error);
    }
}