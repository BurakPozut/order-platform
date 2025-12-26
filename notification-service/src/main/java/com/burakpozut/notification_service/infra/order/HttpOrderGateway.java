package com.burakpozut.notification_service.infra.order;

import java.util.UUID;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.notification_service.domain.gateway.OrderGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
  public UUID getOrderCustomerId(UUID orderId) {
    try {
      OrderCustomerIdResponse response = webClient.get()
          .uri("/api/orders/{id}", orderId)
          .retrieve()
          .bodyToMono(OrderCustomerIdResponse.class)
          .block();
      return response.customerId();
    } catch (WebClientResponseException.NotFound e) {
      log.error("Order not found");

      throw new ExternalServiceNotFoundException("Order not found with id: " + orderId);
    } catch (WebClientResponseException e) {
      log.error("Order service error for order {}: {} - {}",
          orderId, e.getStatusCode(), e.getMessage());

      throw new ExternalServiceException("Order service returned error: " + e.getStatusCode(), e);
    } catch (Exception e) {
      log.error("Failed to communicate with order service for order: {}: {}",
          orderId, e.getMessage());
      throw new ExternalServiceException("Order service is unavailable");
    }
  }

}
