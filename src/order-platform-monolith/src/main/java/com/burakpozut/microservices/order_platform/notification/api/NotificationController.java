package com.burakpozut.microservices.order_platform.notification.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.microservices.order_platform.notification.api.dto.NotificationResponse;
import com.burakpozut.microservices.order_platform.notification.application.service.GetAllNotificationsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final GetAllNotificationsService getAllNotificationsService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getAll() {
    var notifications = getAllNotificationsService.handle();
    var response = notifications.stream().map(NotificationResponse::from).collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }
}
