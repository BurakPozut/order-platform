package com.burakpozut.customer_service.domain;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

  Optional<Customer> findById(UUID id);

}
