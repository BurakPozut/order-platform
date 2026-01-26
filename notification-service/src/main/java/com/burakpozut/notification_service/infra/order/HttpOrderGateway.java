package com.burakpozut.notification_service.infra.order;

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
import com.burakpozut.notification_service.domain.gateway.OrderGateway;

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
    public UUID getOrderCustomerId(UUID orderId) {
        try {
            OrderCustomerIdResponse response = retry
                    .executeSupplier(() -> circuitBreaker.executeSupplier(() -> restClient.get()
                            .uri("/api/orders/{id}", orderId)
                            .header("X-Source-Service", "notification-service")
                            .header("X-Trace-Id", safeTraceId())
                            .retrieve()
                            .body(OrderCustomerIdResponse.class)));
            return response.customerId();
        } catch (RestClientResponseException e) {
            throw mapResponseError(e, orderId);
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(orderId);
            return null; // unreachable
        } catch (Exception e) {
            throw mapNetworkError(e, orderId);
        }
    }

    private String safeTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }

    private void handleCircuitBreakerOpenFallback(UUID orderId) {
        log.warn("order.service.circuit_breaker_open orderId={} action=skip_request",
                orderId);
        throw new ExternalServiceException(
                "Order service circuit breaker is open - cannot fetch order: " + orderId);
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