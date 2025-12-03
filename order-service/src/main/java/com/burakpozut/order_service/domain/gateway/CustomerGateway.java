package com.burakpozut.order_service.domain.gateway;

import java.util.UUID;

public interface CustomerGateway {
  /**
   * Validates if a customer exists in the customer service
   * 
   * @param customerId The customer ID to validate
   * @return true if customer exists, false otherwise
   * @throws CustomerServiceException if the customer service is unavailable
   */
  boolean validateCustomerExists(UUID customerId);
}
