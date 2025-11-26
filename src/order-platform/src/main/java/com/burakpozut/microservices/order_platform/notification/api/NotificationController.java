package com.burakpozut.microservices.order_platform.notification.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform.notification.api.dto.CreateNotificationRequest;
import com.burakpozut.microservices.order_platform.notification.api.dto.NotificationResponse;
import com.burakpozut.microservices.order_platform.notification.application.command.CreateNotificationCommand;
import com.burakpozut.microservices.order_platform.notification.application.service.CreateNotificationService;
import com.burakpozut.microservices.order_platform.notification.application.service.GetAllNotificationsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final GetAllNotificationsService getAllNotificationsService;
  private final CreateNotificationService createNotificationService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getAll() {
    var notifications = getAllNotificationsService.handle();
    var response = notifications.stream().map(NotificationResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<NotificationResponse> create(@Valid @RequestBody CreateNotificationRequest request) {
    var command = new CreateNotificationCommand(
        request.customerId(), request.orderId(), request.type(), request.channel(), request.status());

    var notification = createNotificationService.handle(command);
    return ResponseEntity.ok(NotificationResponse.from(notification));

  }
}
