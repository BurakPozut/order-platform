package com.burakpozut.order_service.infra.persistance.failed_event;

public enum FailedEventStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
