package com.burakpozut.microservices.order_platform.customer.api.dto;

import java.util.UUID;

import com.burakpozut.microservices.order_platform.customer.domain.Customer;

public record CustomerResponse(
        UUID id,
        String fullName,
        String email) {
    public static CustomerResponse from(
            Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail());
    }
}
