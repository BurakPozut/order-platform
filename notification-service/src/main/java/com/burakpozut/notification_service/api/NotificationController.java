package com.burakpozut.notification_service.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.notification_service.api.dto.response.NotificationResponse;
import com.burakpozut.notification_service.app.service.GetAllNotificationsService;
import com.burakpozut.notification_service.app.service.GetNotificationByIdService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final GetAllNotificationsService getAllNotificationsService;
  private final GetNotificationByIdService getNotificationByIdService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getAll() {
    var notifications = getAllNotificationsService.handle();
    var body = notifications.stream().map(NotificationResponse::from).toList();

    return ResponseEntity.ok(body);
  }

  @GetMapping("/{id}")
  public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
    var notification = getNotificationByIdService.handle(id);
    var body = NotificationResponse.from(notification);

    return ResponseEntity.ok(body);
  }

}
