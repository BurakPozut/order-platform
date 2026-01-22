package com.burakpozut.notification_service.infra.elasticsearch;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.burakpozut.notification_service.domain.Notification;
import com.burakpozut.notification_service.domain.NotificationChannel;
import com.burakpozut.notification_service.domain.NotificationStatus;
import com.burakpozut.notification_service.domain.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String customerId;

    @Field(type = FieldType.Keyword)
    private String orderId;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String channel;

    @Field(type = FieldType.Keyword)
    private String status;

    public static NotificationDocument from(Notification notification) {
        return new NotificationDocument(
                notification.id().toString(),
                notification.customerId().toString(),
                notification.orderId().toString(),
                notification.type().name(),
                notification.channel().name(),
                notification.status().name());
    }

    public Notification toDomain() {
        return Notification.rehydrate(
                UUID.fromString(id),
                UUID.fromString(customerId),
                UUID.fromString(orderId),
                NotificationType.valueOf(type),
                NotificationChannel.valueOf(channel),
                NotificationStatus.valueOf(status));
    }
}