package com.burakpozut.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
	// TODO: Add batch in the product service for easier checking in the creation

	// TODO: Add Idempotency in payment as well so we dont create duplicate payments
}
/**
 * TODO: Add optimistic locking
 * Use optimistic locking where concurrent writes could race and you want “last
 * write wins” to fail instead of silently overwriting. In your app the main
 * candidates:
 * Order status transitions (PENDING → CONFIRMED → SHIPPED): add @Version to
 * OrderJpaEntity so two concurrent updates don’t clobber each other.
 * Order item edits (UpdateOrderItemService/UpdateOrderService): if multiple
 * updates can hit the same order around the same time, versioning avoids lost
 * updates.
 * Payment records (in payment-service): guard status changes so a
 * retry/parallel callback doesn’t overwrite a later state. Same idea: @Version
 * on the payment entity.
 * Inventory/stock adjustments (if/when added): classic spot for optimistic
 * locking to prevent double-decrement.
 * You typically don’t need it on read-only queries or on append-only data
 * (e.g., notification send logs). Add @Version to the entities that are updated
 * in-place (orders, payments) to get protection with minimal overhead; only
 * switch to pessimistic locks if you see high contention.
 */