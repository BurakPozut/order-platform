package com.burakpozut.notification_service.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.burakpozut.notification_service.api.dto.request.CreateNotificationRequest;
import com.burakpozut.notification_service.api.dto.response.NotificationResponse;
import com.burakpozut.notification_service.app.service.CreateNotificationService;
import com.burakpozut.notification_service.app.service.GetAllNotificationsService;
import com.burakpozut.notification_service.app.service.GetNotificationByIdService;
import com.burakpozut.notification_service.app.service.SyncNotificationsToElasticsearchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final GetAllNotificationsService getAllNotificationsService;
    private final GetNotificationByIdService getNotificationByIdService;
    private final CreateNotificationService createNotificationService;
    private final SyncNotificationsToElasticsearchService syncNotificationsToElasticsearchService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        log.info("api.notification.getAll.start");
        var notifications = getAllNotificationsService.handle();
        var body = notifications.stream().map(NotificationResponse::from).toList();
        log.info("api.notification.getAll.completed count={}", body.size());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        log.info("api.notification.getById.start notificationId={}", id);
        var notification = getNotificationByIdService.handle(id);
        var body = NotificationResponse.from(notification);
        log.info("api.notification.getById.completed notificationId={}", id);
        return ResponseEntity.ok(body);
    }

    @PostMapping()
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        log.info("api.notification.create.start orderId={} customerId={} type={} channel={} status={}",
                request.orderId(), request.customerId(), request.type(), request.channel(), request.status());
        var command = NotificationMapper.toCommand(request);

        var notification = createNotificationService.handle(command);

        var body = NotificationResponse.from(notification);
        log.info("api.notification.create.completed notificationId={} orderId={}",
                notification.id(), notification.orderId());
        return ResponseEntity.ok(body);

    }

    @PostMapping("/sync-to-elasticsearch")
    public ResponseEntity<SyncNotificationsToElasticsearchService.SyncResult> syncToElasticsearch() {
        log.info("api.notification.sync.start");
        var result = syncNotificationsToElasticsearchService.handle();
        log.info("api.notification.sync.completed totalInDatabase={} syncedToElasticsearch={}",
                result.totalInDatabase(), result.syncedToElasticsearch());
        return ResponseEntity.ok(result);
    }
}
