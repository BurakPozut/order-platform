package com.burakpozut.payment_service.infra.order;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.payment_service.domain.gateway.OrderGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpOrderGateway implements OrderGateway {
    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public HttpOrderGateway(RestClient.Builder builder,
            @Value("${order.service.url}") String orderServiceUrl,
            CircuitBreaker orderCircuitBreaker,
            Retry orderRetry) {
        this.restClient = builder.baseUrl(orderServiceUrl).build();
        this.circuitBreaker = orderCircuitBreaker;
        this.retry = orderRetry;
    }

    @Override
    public void validateOrderId(UUID orderId) {
        try {
            retry.executeRunnable(() -> circuitBreaker.executeRunnable(() -> {
                restClient.get()
                        .uri("/api/orders/{id}", orderId)
                        .header("X-Source-Service", "payment-service")
                        .header("X-Trace-Id", safeTraceId())
                        .retrieve()
                        .toBodilessEntity();
            }));
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(orderId);
        } catch (RestClientResponseException e) {
            throw mapResponseError(e, orderId);
        } catch (Exception e) {
            throw mapNetworkError(e, orderId);
        }
    }

    private String safeTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }

    private void handleCircuitBreakerOpenFallback(UUID orderId) {
        log.warn("order.service.circuit_breaker_open orderId={} action=skip_validation",
                orderId);
        throw new ExternalServiceException(
                "Order service circuit breaker is open - cannot validate order: " + orderId);
    }

    private RuntimeException mapResponseError(RestClientResponseException e, UUID orderId) {
        if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().value() == 404) {
                log.error("order.endpoint.not_found orderId={} statusCode={} message={}",
                        orderId, e.getStatusCode(), e.getMessage());
                return new ExternalServiceNotFoundException("Order service endpoint not found: " + e.getMessage());
            }
            log.error("order.service.client_error orderId={} statusCode={} message={}",
                    orderId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceException("Order service returned client error: " + e.getStatusCode(), e);
        }

        log.error("order.service.server_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Order service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("order.service.communication_failed orderId={} message={}",
                orderId, error.getMessage());
        return new ExternalServiceException("Order service is unavailable", error);
    }
}