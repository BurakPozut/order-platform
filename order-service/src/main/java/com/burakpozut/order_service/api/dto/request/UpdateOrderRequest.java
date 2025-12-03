package com.burakpozut.order_service.api.dto.request;

import java.util.UUID;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.order_service.domain.OrderStatus;

public record UpdateOrderRequest(
    UUID customerId,
    OrderStatus status,
    Currency currency) {

}
