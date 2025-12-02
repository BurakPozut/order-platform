package com.burakpozut.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	// TODO: we used eager in the orderJpaEntity make it more effiecent
	// TODO: Customer id is now unchecked
}
