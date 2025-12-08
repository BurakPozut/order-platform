package com.burakpozut.order_service.infra.notification;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.burakpozut.common.exception.ExternalServiceException;
import com.burakpozut.order_service.domain.gateway.NotificationGateway;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpNotificationGateway implements NotificationGateway {
  private final WebClient webClient;

  private record CreateNotificationRequest(
      UUID customerId,
      UUID orderId,
      String type,
      String channel,
      String status) {
  }

  public HttpNotificationGateway(WebClient.Builder builder,
      @Value("${notification.service.url}") String notificationServiceUrl) {
    this.webClient = builder.baseUrl(notificationServiceUrl).build();
  }

  @Override
  public void sendNotification(UUID customerId, UUID orderId) {
    try {
      var request = new CreateNotificationRequest(customerId, orderId, "ORDER_CONFIRMED",
          "EMAIL", "PENDING");

      webClient.post().uri("/api/notifications")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(request)
          .retrieve()
          .toBodilessEntity()
          .block();
    } catch (WebClientResponseException e) {
      log.error("Notification service error for order {}: {} - {}",
          orderId, e.getStatusCode(), e.getMessage());
      throw new ExternalServiceException("Notification service returned error : " + e.getMessage());
    } catch (Exception e) {
      log.error("Failed to communicate with notification service for order {}: {}",
          orderId, e.getMessage());

      throw new ExternalServiceException("Notification service is unavailable ", e);

    }
  }
}
