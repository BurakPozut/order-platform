package com.burakpozut.customer_service.api.dto.request;

import jakarta.validation.constraints.Email;

public record PatchCustomerRequest(
                String fullName,
                @Email String email) {

}
