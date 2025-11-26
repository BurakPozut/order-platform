package com.burakpozut.microservices.order_platform.customer.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(

    @NotBlank String fullName,

    @NotBlank @Email String email) {
}
