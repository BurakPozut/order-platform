package com.burakpozut.notification_service.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationRepository;
import com.burakpozut.notification_service.infra.elasticsearch.NotificationDocument;
import com.burakpozut.notification_service.infra.elasticsearch.NotificationElasticsearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncNotificationsToElasticsearchService {

    private final NotificationRepository notificationRepository;
    private final NotificationElasticsearchRepository elasticsearchRepository;

    public SyncResult handle() {
        log.info("Starting sync of notifications from database to Elasticsearch...");

        List<Notification> notifications = notificationRepository.findAll();
        log.info("Found {} notifications in database", notifications.size());

        if (notifications.isEmpty()) {
            log.info("No notifications to sync");
            return new SyncResult(0, 0);
        }

        List<NotificationDocument> documents = notifications.stream()
                .map(NotificationDocument::from)
                .toList();

        elasticsearchRepository.saveAll(documents);

        log.info("Successfully synced {} notifications to Elasticsearch", documents.size());

        return new SyncResult(notifications.size(), documents.size());
    }

    public record SyncResult(int totalInDatabase, int syncedToElasticsearch) {
    }
}