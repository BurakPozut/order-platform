package com.burakpozut.notification_service.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Configuration
public class ResilienceConfig {

  @Bean
  public CircuitBreaker orderCircuitBreaker() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .failureRateThreshold(50) // Open circuit after 50%
        .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait for 30 seconds before half open
        .slidingWindowSize(10) // Last 10 calls
        .minimumNumberOfCalls(5) // Needed at least 5 calls before calculating failure rate
        .permittedNumberOfCallsInHalfOpenState(3)
        .build();

    return CircuitBreaker.of("orderService", config);
  }

  @Bean
  public Retry orderRetry() {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofMillis(500))
        .retryOnException(throwable -> {
          if (throwable instanceof CallNotPermittedException) {
            return false;
          }
          if (throwable instanceof WebClientResponseException e) {
            return e.getStatusCode().is5xxServerError();
          }
          return true;
        }).build();

    return Retry.of("orderService", config);
  }

  @Bean
  public CircuitBreaker customerCircuitBreaker() {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .failureRateThreshold(50) // Open circuit after 50%
        .waitDurationInOpenState(Duration.ofSeconds(30)) // Wait for 30 seconds before half open
        .slidingWindowSize(10) // Last 10 calls
        .minimumNumberOfCalls(5) // Needed at least 5 calls before calculating failure rate
        .permittedNumberOfCallsInHalfOpenState(3)
        .build();

    return CircuitBreaker.of("customerService", config);
  }

  @Bean
  public Retry customerRetry() {
    RetryConfig config = RetryConfig.custom()
        .maxAttempts(3)
        .waitDuration(Duration.ofMillis(500))
        .retryOnException(throwable -> {
          if (throwable instanceof CallNotPermittedException) {
            return false;
          }
          if (throwable instanceof WebClientResponseException e) {
            return e.getStatusCode().is5xxServerError();
          }
          return true;
        }).build();

    return Retry.of("customerService", config);
  }

}
