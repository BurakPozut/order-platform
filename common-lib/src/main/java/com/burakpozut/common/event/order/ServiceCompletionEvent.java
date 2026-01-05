package com.burakpozut.common.event.order;

import java.util.UUID;

public record ServiceCompletionEvent(
        UUID orderId,
        String serviceName) {

}
