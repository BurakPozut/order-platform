package com.burakpozut.customer_service.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateCustomerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email) {

}
