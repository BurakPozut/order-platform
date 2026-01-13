package com.burakpozut.product_service.infra.kafka.failed_event;

public enum FailedEventStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
