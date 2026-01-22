package com.burakpozut.notification_service.infra.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationElasticsearchRepository extends ElasticsearchRepository<NotificationDocument, String> {

    List<NotificationDocument> findByCustomerId(String customerId);

    List<NotificationDocument> findByOrderId(String orderId);

    List<NotificationDocument> findByType(String type);

    List<NotificationDocument> findByStatus(String status);

    List<NotificationDocument> findByCustomerIdAndStatus(String customerId, String status);

    List<NotificationDocument> findByOrderIdAndStatus(String orderId, String status);
}