package com.burakpozut.microservices.order_platform_monolith.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

  private String message;
  private String error;
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}
