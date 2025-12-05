package com.burakpozut.payment_service.infra.order;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.payment_service.domain.gateway.OrderGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpOrderGateway implements OrderGateway {
  private final WebClient webClient;

  public HttpOrderGateway(WebClient.Builder builder,
      @Value("${order.service.url}") String orderServiceUrl) {
    this.webClient = builder.baseUrl(orderServiceUrl).build();
  }

  @Override
  public boolean validateOrderId(UUID orderId) {
    try {
      webClient.get().uri("/api/orders/{id}", orderId)
          .retrieve().toBodilessEntity().block();
      return true;
    } catch (WebClientResponseException.NotFound e) {
      log.error("Order not found with id: {}", orderId);
      return false;
    } catch (WebClientResponseException e) {
      log.error("Order service error for order {}: {} - {}",
          orderId, e.getStatusCode(), e.getMessage());
      throw new ExternalServiceException("Order service returned error: " + e.getStatusCode(), e);
    } catch (Exception e) {
      log.error("Falied to communicate with order servoce for order {}: {}",
          orderId, e.getMessage());

      throw new ExternalServiceException("Order service is unavailable");
    }
  }

}
