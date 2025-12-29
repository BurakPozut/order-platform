package com.burakpozut.order_service.app.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.burakpozut.common.domain.Currency;
import com.burakpozut.common.exception.DomainValidationException;
import com.burakpozut.common.exception.ExternalServiceNotFoundException;
import com.burakpozut.order_service.app.command.UpdateOrderCommand;
import com.burakpozut.order_service.app.exception.OrderNotFoundException;
import com.burakpozut.order_service.app.exception.customer.CustomerNotFoundException;
import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderRepository;
import com.burakpozut.order_service.domain.OrderStatus;
import com.burakpozut.order_service.domain.gateway.CustomerGateway;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateOrderService {
  private final OrderRepository orderRepository;
  private final CustomerGateway customerGateway;

  private record ResolvedOrderFields(
      UUID customerId,
      OrderStatus status,
      Currency currency) {
  }

  public Order handle(UUID id, UpdateOrderCommand command) {
    validate(command);

    var existing = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));

    // Apply updates only to provided fields
    var resolved = resolveFields(command, existing);

    var updated = Order.rehydrate(existing.id(),
        resolved.customerId, resolved.status,
        existing.totalAmount(), resolved.currency, existing.items(), existing.idempotencyKey(), existing.updatedAt());
    return orderRepository.save(updated, false);
  }

  private ResolvedOrderFields resolveFields(UpdateOrderCommand command,
      Order existing) {

    if (command.customerId() != null) {
      validateCustomerExists(command.customerId());
    }
    return new ResolvedOrderFields(
        command.customerId() != null ? command.customerId() : existing.customerId(),
        command.status() != null ? command.status() : existing.status(),
        command.currency() != null ? command.currency() : existing.currency());
  }

  private boolean validate(UpdateOrderCommand command) {
    if (command.customerId() == null &&
        command.status() == null &&
        command.currency() == null) {
      throw new DomainValidationException("At least one field must be provided");
    }
    return true;
  }

  private void validateCustomerExists(UUID customerId) {
    try {
      customerGateway.validateCustomerExists(customerId);
    } catch (ExternalServiceNotFoundException e) {
      throw new CustomerNotFoundException(customerId);
    }
  }

}
