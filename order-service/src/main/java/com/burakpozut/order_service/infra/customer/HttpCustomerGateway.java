package com.burakpozut.order_service.infra.customer;

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
import com.burakpozut.order_service.domain.gateway.CustomerGateway;

import lombok.extern.slf4j.Slf4j;

// TODO: make this change in every http clint we are no longer using webClient
// TODO: in kafka logs we lost the traceId?
@Component
@Slf4j
public class HttpCustomerGateway implements CustomerGateway {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public HttpCustomerGateway(RestClient.Builder builder, @Value("${customer.service.url}") String customerServiceUrl,
            CircuitBreaker customerCircuitBreaker,
            Retry customerRetry) {
        this.circuitBreaker = customerCircuitBreaker;
        this.retry = customerRetry;
        this.restClient = builder.baseUrl(customerServiceUrl).build();
    }

    @Override
    public void validateCustomerExists(UUID customerId) {
        try {
            retry.executeRunnable(() -> circuitBreaker.executeRunnable(() -> {
                restClient.get()
                        .uri("/api/customers/{id}", customerId)
                        .header("X-Source-Service", "order-service")
                        .header("X-Trace-Id", safeTraceId())
                        .retrieve()
                        .toBodilessEntity();
            }));
        } catch (CallNotPermittedException e) {
            handleCircuitBreakerOpenFallback(customerId);
        } catch (RestClientResponseException e) {
            throw mapResponseError(e, customerId);
        } catch (Exception e) {
            throw mapNetworkError(e, customerId);
        }
    }

    private String safeTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "" : traceId;
    }

    private void handleCircuitBreakerOpenFallback(UUID customerId) {
        log.warn("customer.service.circuit_breaker_open customerId={} action=skip_validation",
                customerId);
        throw new ExternalServiceException(
                "Customer service circuit breaker is open - cannot validate customer: " + customerId);
    }

    private ExternalServiceException mapResponseError(RestClientResponseException e, UUID customerId) {
        if (e.getStatusCode().is4xxClientError()) {
            if (e.getStatusCode().value() == 404) {
                log.error("customer.endpoint.not_found customerId={} statusCode={} message={}",
                        customerId, e.getStatusCode(), e.getMessage());

                // TODO: make this ExternalServiceNotFound exception
                return new ExternalServiceException("Customer service endpoint not found: " + e.getMessage());
            }
            log.error("customer.service.client_error customerId={} statusCode={} message={}",
                    customerId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceException("Customer service returned client error: " + e.getStatusCode(), e);
        }

        log.error("customer.service.server_error customerId={} statusCode={} message={}",
                customerId, e.getStatusCode(), e.getMessage());
        return new ExternalServiceException("Customer service returned server error: " + e.getStatusCode(), e);
    }

    private ExternalServiceException mapNetworkError(Throwable error, UUID customerId) {
        log.error("customer.service.communication_failed customerId={} message={}",
                customerId, error.getMessage());
        return new ExternalServiceException("Customer service is unavailable", error);
    }
    // @Override
    // public void validateCustomerExists(UUID customerId) {
    // String traceId = MDC.get("traceId");
    // webClient.get().uri("/api/customers/{id}", customerId)
    // .header("X-Source-Service", "order-service")
    // .header("X-Trace-Id", traceId == null ? "" : traceId)
    // .retrieve().toBodilessEntity()
    // .transformDeferred(RetryOperator.of(retry))
    // .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    // .onErrorResume(CallNotPermittedException.class, e ->
    // handleCircuitBreakerOpenFallback(customerId))
    // .onErrorResume(ExternalServiceException.class, e ->
    // handleServiceUnavailableFallback(customerId))
    // .onErrorMap(error -> mapError(error, customerId))
    // .block();
    // }

    // private Mono<ResponseEntity<Void>> handleCircuitBreakerOpenFallback(UUID
    // customerId) {
    // log.warn("customer.service.circuit_breaker_open customerId={}
    // action=skip_validation",
    // customerId);
    // return Mono.error(new ExternalServiceException(
    // "Customer service circuit breaker is open - cannot validate customer: " +
    // customerId));

    // }

    // private Mono<ResponseEntity<Void>> handleServiceUnavailableFallback(UUID
    // customerId) {
    // log.warn("customer.service.unavailable customerId={} action=skip_validation",
    // customerId);
    // return Mono.error(new ExternalServiceException(
    // "Customer service is unavailable - cannot validate customer: " +
    // customerId));
    // }// TODO: change these return these are for a test

    // private Throwable mapError(Throwable error, UUID orderId) {
    // if (error instanceof WebClientResponseException.NotFound e) {
    // return mapNotFoundError(e, orderId);
    // } else if (error instanceof WebClientResponseException e) {
    // return e.getStatusCode().is5xxServerError()
    // ? mapServerError(e, orderId)
    // : mapClientError(e, orderId);
    // } else {
    // return mapNetworkError(error, orderId);
    // }
    // }

    // private ExternalServiceNotFoundException
    // mapNotFoundError(WebClientResponseException.NotFound e, UUID customerId) {
    // log.error("customer.endpoint.not_found customerId={} statusCode={}
    // message={}",
    // customerId, e.getStatusCode(), e.getMessage());
    // return new ExternalServiceNotFoundException("Customer service endpoint not
    // found: " + e.getMessage());
    // }

    // private ExternalServiceException mapServerError(WebClientResponseException e,
    // UUID customerId) {
    // log.error("customer.service.server_error customerId={} statusCode={}
    // message={}",
    // customerId, e.getStatusCode(), e.getMessage());
    // return new ExternalServiceException("Customer service returned server error:
    // " + e.getStatusCode(), e);
    // }

    // private ExternalServiceException mapClientError(WebClientResponseException e,
    // UUID customerId) {
    // log.error("customer.service.client_error customerId={} statusCode={}
    // message={}",
    // customerId, e.getStatusCode(), e.getMessage());
    // return new ExternalServiceException("Customer service returned client error:
    // " + e.getStatusCode(), e);
    // }

    // private ExternalServiceException mapNetworkError(Throwable error, UUID
    // customerId) {
    // log.error("customer.service.communication_failed customerId={} message={}",
    // customerId, error.getMessage());
    // return new ExternalServiceException("Customer service is unavailable",
    // error);
    // }
}
