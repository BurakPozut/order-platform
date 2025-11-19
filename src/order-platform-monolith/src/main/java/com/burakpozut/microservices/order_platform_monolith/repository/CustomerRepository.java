package com.burakpozut.microservices.order_platform_monolith.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.burakpozut.microservices.order_platform_monolith.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  Optional<Customer> findByEmail(@NonNull String email);

  List<Customer> findByStatus(String status);

  @Query("SELECT c FROM Customer c WHERE c.status = :status")
  List<Customer> findCustomersByStatus(@Param("status") String status);
}
