package com.burakpozut.order_service.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientResponseException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Configuration
public class ResilienceConfig {

    // Circuit Breaker Common Configuration
    private static final int FAILURE_RATE_THRESHOLD = 50;
    private static final Duration WAIT_DURATION = Duration.ofSeconds(30);
    private static final int SLIDING_WINDOW_SIZE = 10;
    private static final int MINIMUM_NUMBER_OF_CALLS = 5;
    private static final int PERMITTED_CALLS_IN_HALF_OPEN = 3;

    // Retry Common Configuration
    private static final int RETRY_MAX_ATTEMPTS = 3;
    private static final Duration RETRY_WAIT_DURATION = Duration.ofMillis(500);

    @Bean
    public CircuitBreaker paymentCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD) // Open circuit after 50%
                .waitDurationInOpenState(WAIT_DURATION) // Wait for 30 seconds before half open
                .slidingWindowSize(SLIDING_WINDOW_SIZE) // Last 10 calls
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS) // Needed at least 5 calls before calculating failure
                                                               // rate
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return CircuitBreaker.of("paymentService", config);
    }

    @Bean
    public Retry paymentRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    if (throwable instanceof RestClientResponseException e) {
                        return e.getStatusCode().is5xxServerError();
                    }
                    return true;
                }).build();

        return Retry.of("paymentService", config);
    }

    @Bean
    public CircuitBreaker notificationCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(WAIT_DURATION)
                .slidingWindowSize(SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return CircuitBreaker.of("notificationService", config);
    }

    @Bean
    public Retry notificationRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    if (throwable instanceof RestClientResponseException e) {
                        return e.getStatusCode().is5xxServerError();
                    }
                    return true;
                }).build();

        return Retry.of("notificationService", config);
    }

    @Bean
    public CircuitBreaker customerCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(WAIT_DURATION)
                .slidingWindowSize(SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return CircuitBreaker.of("customerService", config);
    }

    @Bean
    public Retry customerRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    if (throwable instanceof RestClientResponseException e) {
                        return e.getStatusCode().is5xxServerError();
                    }
                    return true;
                }).build();

        return Retry.of("customerService", config);
    }

    @Bean
    public CircuitBreaker productCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(FAILURE_RATE_THRESHOLD)
                .waitDurationInOpenState(WAIT_DURATION)
                .slidingWindowSize(SLIDING_WINDOW_SIZE)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .permittedNumberOfCallsInHalfOpenState(PERMITTED_CALLS_IN_HALF_OPEN)
                .build();

        return CircuitBreaker.of("productService", config);
    }

    @Bean
    public Retry productRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .waitDuration(RETRY_WAIT_DURATION)
                .retryOnException(throwable -> {
                    if (throwable instanceof CallNotPermittedException) {
                        return false;
                    }
                    if (throwable instanceof RestClientResponseException e) {
                        return e.getStatusCode().is5xxServerError();
                    }
                    return true;
                }).build();

        return Retry.of("productService", config);
    }
}
