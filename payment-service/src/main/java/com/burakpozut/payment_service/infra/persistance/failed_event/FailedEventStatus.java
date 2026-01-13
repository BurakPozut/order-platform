package com.burakpozut.payment_service.infra.persistance.failed_event;

public enum FailedEventStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
