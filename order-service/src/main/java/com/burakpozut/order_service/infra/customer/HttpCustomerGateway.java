package com.burakpozut.order_service.infra.customer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.burakpozut.order_service.app.exception.customer.CustomerServiceException;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpCustomerGateway implements CustomerGateway {

  private final WebClient webClient;

  public HttpCustomerGateway(WebClient.Builder builder,
      @Value("${customer.service.url}") String customerServiceUrl) {
    this.webClient = builder.baseUrl(customerServiceUrl).build();
  }

  @Override
  public boolean validateCustomerExists(UUID customerId) {
    try {
      webClient.get().uri("/api/customers/{id}", customerId).retrieve().toBodilessEntity().block();
      return true;
    } catch (WebClientResponseException.NotFound e) {
      return false;
    } catch (WebClientResponseException e) {
      log.error("Customer service error for customer {}: {} - ",
          customerId, e.getStatusCode(), e.getMessage());
      throw new CustomerServiceException("Customer service returned error: " + e.getStatusCode(), e);
    } catch (Exception e) {
      log.error("Failed to communicate with customer service for customer {}: {}",
          customerId, e.getMessage());
      throw new CustomerServiceException("Customer service is unavailable", e);
    }
  }
}
