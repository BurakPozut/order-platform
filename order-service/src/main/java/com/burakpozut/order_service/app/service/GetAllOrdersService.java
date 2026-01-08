package com.burakpozut.order_service.app.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.burakpozut.order_service.domain.Order;
import com.burakpozut.order_service.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllOrdersService {
    private final OrderRepository orderRepository;

    public List<Order> handle() {
        return orderRepository.findAll();
    }

    public Slice<Order> handle(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
