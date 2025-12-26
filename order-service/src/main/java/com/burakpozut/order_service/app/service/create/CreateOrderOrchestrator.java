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
    CreationResult result = orderCreationService.create(command);
    try {
      if (result.isNew())
        orderSideEffectsService.trigger(result.order());
    } catch (Exception e) {
      log.error("Failed to trigger effects for order {}, Rolling back (Cancelling)", result.order().id(), e);
      cancleOrderService.handle(result.order().id());
      throw e;
    }
    return result.order();
  }

}
