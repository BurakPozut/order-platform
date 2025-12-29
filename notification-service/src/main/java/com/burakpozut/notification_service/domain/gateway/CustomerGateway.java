package com.burakpozut.notification_service.domain.gateway;

import java.util.UUID;

public interface CustomerGateway {

  void validateCustomerExists(UUID customerId);
}
