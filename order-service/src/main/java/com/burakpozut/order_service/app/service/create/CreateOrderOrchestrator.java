package com.burakpozut.order_service.app.service.create;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.app.command.CreateOrderCommand;
import com.burakpozut.order_service.app.service.CancelOrderService;
import com.burakpozut.order_service.app.service.create.OrderCreationService.CreationResult;
import com.burakpozut.order_service.domain.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderOrchestrator {
    private final OrderCreationService orderCreationService;
    private final OrderSideEffectsService orderSideEffectsService;
    private final CancelOrderService cancleOrderService;

    public Order handle(CreateOrderCommand command) {
        log.info("order.create.start customerId={} items={}",
                command.customerId(), command.items() != null ? command.items().size() : 0);

        CreationResult result = orderCreationService.create(command);

        if (result.isNew()) {
            log.info("order.create.created orderId={} customerId={} total={}",
                    result.order().id(), result.order().customerId(), result.order().totalAmount());
        } else {
            log.info("order.create.idempotent_hit orderId={} customerId={}",
                    result.order().id(), result.order().customerId());
        }

        try {
            if (result.isNew()) {
                log.debug("order.create.sideEffects.start orderId={}", result.order().id());
                orderSideEffectsService.trigger(result.order());
                log.debug("order.create.sideEffects.done orderId={}", result.order().id());
            }
        } catch (Exception e) {
            log.error("order.create.sideEffects.failed orderId={} action=cancel",
                    result.order().id(), e);
            cancleOrderService.handle(result.order().id());
            log.warn("order.create.cancelled orderId={}", result.order().id());
            throw e;
        }

        return result.order();
    }

}
