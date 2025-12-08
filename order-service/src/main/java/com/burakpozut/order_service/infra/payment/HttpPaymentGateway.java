package com.burakpozut.order_service.infra.payment;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.order_service.domain.gateway.PaymentGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpPaymentGateway implements PaymentGateway {
  private final WebClient webClient;

  private record CreatePaymentRequest(
      UUID orderId,
      BigDecimal amount,
      Currency currency,
      String provider,
      String providerRef) {
  }

  public HttpPaymentGateway(WebClient.Builder builder,
      @Value("${payment.service.url}") String paymentServiceUrl) {
    this.webClient = builder.baseUrl(paymentServiceUrl).build();
  }

  @Override
  public void createPayment(UUID orderId, BigDecimal amount, Currency currency, String provider, String providerRef) {
    try {
      var request = new CreatePaymentRequest(orderId, amount,
          currency, provider, providerRef);
      webClient.post().uri("/api/payments")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .retrieve()
          .toBodilessEntity()
          .block();
    } catch (WebClientResponseException e) {
      log.error("Payment service erro for order {}: {} - {}",
          orderId, e.getStatusCode(), e.getMessage());
      throw new ExternalServiceException("Payment service returned error: " + e.getMessage());
    } catch (Exception e) {
      log.error("Failed to communicate with Payment service for order {}: {}",
          orderId, e.getMessage());
      throw new ExternalServiceException("Payment service is unavailable", e);
    }

  }

}
