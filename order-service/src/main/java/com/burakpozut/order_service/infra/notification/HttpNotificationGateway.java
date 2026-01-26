package com.burakpozut.order_service.infra.notification;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.gateway.NotificationGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpNotificationGateway implements NotificationGateway {
    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    private record CreateNotificationRequest(
            UUID customerId,
            UUID orderId,
            String type,
            String channel,
            String status) {
    }

    public HttpNotificationGateway(RestClient.Builder builder,
            @Value("${notification.service.url}") String notificationServiceUrl,
            CircuitBreaker notificationCircuitBreaker,
            Retry notificationRetry) {
        this.restClient = builder.baseUrl(notificationServiceUrl).build();
        this.circuitBreaker = notificationCircuitBreaker;
        this.retry = notificationRetry;
    }

    @Override
    public void sendNotification(UUID customerId, UUID orderId) {
        var request = new CreateNotificationRequest(customerId, orderId, "ORDER_CONFIRMED",
                "EMAIL", "PENDING");

        try {
            retry.executeRunnable(() -> circuitBreaker.executeRunnable(() -> {
                restClient.post()
                        .uri("/api/notifications")
                        .header("X-Source-Service", "order-service")
                        .header("X-Trace-Id", safeTraceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
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
        log.warn("notification.service.circuit_breaker_open orderId={} action=skip_notification",
                orderId);
        throw new ExternalServiceException(
                "Notification service circuit breaker is open - cannot send notification: " + orderId);
    }

    private RuntimeException mapResponseError(RestClientResponseException e, UUID orderId) {
        if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().value() == 404) {
                log.error("notification.endpoint.not_found orderId={} statusCode={} message={}",
                        orderId, e.getStatusCode(), e.getMessage());
                return new ExternalServiceNotFoundException(
                        "Notification service endpoint not found: " + e.getMessage());
            }
            log.error("notification.service.client_error orderId={} statusCode={} message={}",
                    orderId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceException(
                    "Notification service returned client error: " + e.getStatusCode(), e);
        }

        log.error("notification.service.server_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException(
                "Notification service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("notification.service.communication_failed orderId={} message={}",
                orderId, error.getMessage());
        return new ExternalServiceException("Notification service is unavailable", error);
    }
}