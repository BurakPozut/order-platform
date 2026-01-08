package com.burakpozut.common.event.order;

import java.util.UUID;

import com.burakpozut.common.domain.ServiceName;

public record ServiceCompletionEvent(
        UUID orderId,
        ServiceName serviceName) {
    public static ServiceCompletionEvent of(UUID orderId, ServiceName serviceName) {
        return new ServiceCompletionEvent(orderId, serviceName);
    }

}
