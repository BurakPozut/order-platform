package com.burakpozut.microservices.order_platform_monolith.dto;

import java.time.LocalDateTime;

import com.burakpozut.microservices.order_platform_monolith.entity.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCustomerRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Full name is required")
  private String fullName;

  private String status; // TODO: check if this field is protected for the sql injections

  public Customer toEntity() {
    Customer customer = new Customer();
    customer.setEmail(this.email);
    customer.setFullName(this.fullName);
    customer.setStatus(this.status != null ? this.status : "ACTIVE");
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());
    return customer;
  }
}
