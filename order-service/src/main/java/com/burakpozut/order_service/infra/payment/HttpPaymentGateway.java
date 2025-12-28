package com.burakpozut.order_service.infra.payment;

import java.math.BigDecimal;
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

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;

import lombok.extern.slf4j.Slf4j;

// TODO: Add resilience here
@Component
@Slf4j
public class HttpPaymentGateway implements PaymentGateway {
  private final WebClient webClient;
  private final CircuitBreaker circuitBreaker;
  private final Retry retry;

  private record CreatePaymentRequest(
      UUID orderId,
      BigDecimal amount,
      Currency currency,
      String provider,
      String providerRef) {
  }

  public HttpPaymentGateway(WebClient.Builder builder,
      @Value("${payment.service.url}") String paymentServiceUrl,
      CircuitBreaker paymentCircuitBreaker,
      Retry paymentRetry) {
    this.webClient = builder.baseUrl(paymentServiceUrl).build();
    this.circuitBreaker = paymentCircuitBreaker;
    this.retry = paymentRetry;
  }

  @Override
  public void createPayment(UUID orderId, BigDecimal amount, Currency currency, String provider, String providerRef) {
    // try {
    var request = new CreatePaymentRequest(orderId, amount,
        currency, provider, providerRef);
    webClient.post().uri("/api/payments")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .retrieve()
        .toBodilessEntity()
        .transformDeferred(RetryOperator.of(retry))
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
        .onErrorMap(error -> {
          if (error instanceof WebClientResponseException.NotFound e) {
            log.error("Payment endpoint not found for order {}: {} - {}",
                orderId, e.getStatusCode(), e.getMessage());
            return new ExternalServiceNotFoundException("Payment service endpoint not found: " + e.getMessage());
          } else if (error instanceof WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
              log.error("Payment service server error for order {}: {} - {}",
                  orderId, e.getStatusCode(), e.getMessage());
              return new ExternalServiceException("Payment service returned server error: " + e.getStatusCode(), e);
            } else {
              // 4xx client errors (except 404 which is handled above)
              log.error("Payment service client error for order {}: {} - {}",
                  orderId, e.getStatusCode(), e.getMessage());
              return new ExternalServiceException("Payment service returned client error: " + e.getStatusCode(), e);
            }
          } else {
            // Network errors, timeouts, connection refused, etc.
            log.error("Failed to communicate with Payment service for order {}: {}",
                orderId, error.getMessage());
            return new ExternalServiceException("Payment service is unavailable", error);
          }
        })
        .block();
    // }
    // catch (WebClientResponseException e) {
    // log.error("Payment service error for order {}: {} - {}",
    // orderId, e.getStatusCode(), e.getMessage());
    // throw new ExternalServiceException("Payment service returned error: " +
    // e.getMessage());
    // }
    // catch (Exception e) {
    // log.error("Failed to communicate with Payment service for order {}: {}",
    // orderId, e.getMessage());
    // throw new ExternalServiceException("Payment service is unavailable", e);
    // }

  }

}
