package com.burakpozut.order_service.app.service.create;

import org.springframework.stereotype.Service;

import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.OrderConfirmationState;
import com.burakpozut.order_service.domain.OrderConfirmationStateRepository;
import com.burakpozut.order_service.infra.kafka.OrderCreatedPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSideEffectsService {

    private final OrderCreatedPublisher orderConfirmedPublisher;
    private final OrderConfirmationStateRepository confirmationStateRepository;

    public void trigger(Order order) {
        orderConfirmedPublisher.publish(order);
        OrderConfirmationState initialState = OrderConfirmationState.createFor(
                order.id());

        confirmationStateRepository.save(initialState, true);

    }

}
