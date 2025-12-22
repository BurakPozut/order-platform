package com.burakpozut.payment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
	// TODO: right now any body can create payment with invalid order Id we need to
	// make sure this doesnt happen by some way
	// use api keys just for this
}
