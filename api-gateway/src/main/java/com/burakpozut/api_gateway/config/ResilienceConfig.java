package com.burakpozut.api_gateway.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Configuration
public class ResilienceConfig {

    // Circuit Breaker Common Configuration
    private static final int CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD = 50;
    private static final Duration CIRCUIT_BREAKER_WAIT_DURATION = Duration.ofSeconds(10);
    private static final int CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE = 10;
    private static final int CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS = 5;
    private static final int CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN = 3;

    // Retry Common Configuration
    private static final int RETRY_MAX_ATTEMPTS = 3;
    private static final Duration RETRY_WAIT_DURATION = Duration.ofMillis(500);

    @Bean
    public CircuitBreaker customerServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(CIRCUIT_BREAKER_WAIT_DURATION)
                .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return registry.circuitBreaker("customerService", config);
    }

    @Bean
    public CircuitBreaker productServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(CIRCUIT_BREAKER_WAIT_DURATION)
                .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return registry.circuitBreaker("productService", config);
    }

    @Bean
    public CircuitBreaker orderServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(CIRCUIT_BREAKER_WAIT_DURATION)
                .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return registry.circuitBreaker("orderService", config);
    }

    @Bean
    public CircuitBreaker paymentServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(CIRCUIT_BREAKER_WAIT_DURATION)
                .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return registry.circuitBreaker("paymentService", config);
    }

    @Bean
    public CircuitBreaker notificationServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(CIRCUIT_BREAKER_FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(CIRCUIT_BREAKER_WAIT_DURATION)
                .slidingWindowSize(CIRCUIT_BREAKER_SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(CIRCUIT_BREAKER_MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(CIRCUIT_BREAKER_PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return registry.circuitBreaker("notificationService", config);
    }

    // Retry Beans
    @Bean
    public Retry customerServiceRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    // Don't retry if circuit breaker is open
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    // Retry on network/IO errors (these are transient)
                    return throwable instanceof IOException
                            || throwable instanceof RuntimeException;
                })
                .build();

        return registry.retry("customerService", config);
    }

    @Bean
    public Retry productServiceRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    // Don't retry if circuit breaker is open
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    // Retry on network/IO errors (these are transient)
                    return throwable instanceof IOException
                            || throwable instanceof RuntimeException;
                })
                .build();

        return registry.retry("productService", config);
    }

    @Bean
    public Retry orderServiceRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    // Don't retry if circuit breaker is open
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    // Retry on network/IO errors (these are transient)
                    return throwable instanceof IOException
                            || throwable instanceof RuntimeException;
                })
                .build();

        return registry.retry("orderService", config);
    }

    @Bean
    public Retry paymentServiceRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    // Don't retry if circuit breaker is open
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    // Retry on network/IO errors (these are transient)
                    return throwable instanceof IOException
                            || throwable instanceof RuntimeException;
                })
                .build();

        return registry.retry("paymentService", config);
    }

    @Bean
    public Retry notificationServiceRetry(RetryRegistry registry) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    // Don't retry if circuit breaker is open
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    // Retry on network/IO errors (these are transient)
                    return throwable instanceof IOException
                            || throwable instanceof RuntimeException;
                })
                .build();

        return registry.retry("notificationService", config);
    }
}
