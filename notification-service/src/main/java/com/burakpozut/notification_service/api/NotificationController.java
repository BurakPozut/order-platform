package com.burakpozut.notification_service.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.notification_service.api.dto.response.NotificationResponse;
import com.burakpozut.notification_service.app.service.GetAllNotificationsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
  private final GetAllNotificationsService getAllNotificationsService;

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getAll() {
    var notifications = getAllNotificationsService.handle();
    var body = notifications.stream().map(NotificationResponse::from).toList();

    return ResponseEntity.ok(body);
  }

}
