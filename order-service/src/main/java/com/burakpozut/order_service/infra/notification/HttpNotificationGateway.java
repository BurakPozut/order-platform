package com.burakpozut.order_service.infra.notification;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.gateway.NotificationGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpNotificationGateway implements NotificationGateway {
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    private record CreateNotificationRequest(
            UUID customerId,
            UUID orderId,
            String type,
            String channel,
            String status) {
    }

    public HttpNotificationGateway(WebClient.Builder builder,
            @Value("${notification.service.url}") String notificationServiceUrl,
            CircuitBreaker notificationCircuitBreaker,
            Retry notificationRetry) {
        this.webClient = builder.baseUrl(notificationServiceUrl).build();
        this.circuitBreaker = notificationCircuitBreaker;
        this.retry = notificationRetry;
    }

    @Override
    public void sendNotification(UUID customerId, UUID orderId) {
        var request = new CreateNotificationRequest(customerId, orderId, "ORDER_CONFIRMED",
                "EMAIL", "PENDING");

        webClient.post().uri("/api/notifications").header("X-Source-Service", "order-service")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorMap(error -> mapError(error, orderId))
                .block();
    }

    private Throwable mapError(Throwable error, UUID orderId) {
        if (error instanceof WebClientResponseException.NotFound e) {
            return mapNotFoundError(e, orderId);
        } else if (error instanceof WebClientResponseException e) {
            return e.getStatusCode().is5xxServerError()
                    ? mapServerError(e, orderId)
                    : mapClientError(e, orderId);
        } else {
            return mapNetworkError(error, orderId);
        }
    }

    private ExternalServiceNotFoundException mapNotFoundError(WebClientResponseException.NotFound e, UUID orderId) {
        log.error("notification.endpoint.not_found orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceNotFoundException("Notification service endpoint not found: " + e.getMessage());
    }

    private ExternalServiceException mapServerError(WebClientResponseException e, UUID orderId) {
        log.error("notification.service.server_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Notification service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapClientError(WebClientResponseException e, UUID orderId) {
        log.error("notification.service.client_error orderId={} statusCode={} message={}",
                orderId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Notification service returned client error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID orderId) {
        log.error("notification.service.communication_failed orderId={} message={}",
                orderId, error.getMessage());
        return new ExternalServiceException("Notification service is unavailable", error);
    }
}
