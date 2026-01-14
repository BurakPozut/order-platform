package com.burakpozut.api_gateway.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class CircuitBreakerFilter extends OncePerRequestFilter {
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String serviceName = getServiceName(path);

        if (serviceName == null) {
            filterChain.doFilter(request, response);
            return;
        }

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(serviceName);
        Retry retry = retryRegistry.retry(serviceName);

        try {
            circuitBreaker.executeRunnable(() -> {
                retry.executeRunnable(() -> {
                    try {
                        filterChain.doFilter(request, response);
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (CallNotPermittedException e) {
            // Circuit breaker is OPEN - fast failure
            handleCircuitBreakerOpen(response, serviceName);
        } catch (RuntimeException e) {
            // Retry exhausted or other runtime errors
            Throwable cause = e.getCause();
            if (cause instanceof IOException || cause instanceof ServletException) {
                // Service communication error after retries
                handleServiceError(response, serviceName, e);
            } else {
                // Unexpected error
                handleUnexpectedError(response, serviceName, e);
            }
        } catch (Exception e) {
            // Catch-all for any other checked exceptions (shouldn't happen, but safety net)
            handleUnexpectedError(response, serviceName, e);
        }
    }

    private void handleCircuitBreakerOpen(HttpServletResponse response, String serviceName) {
        try {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    String.format(
                            "{\"error\":\"Service Unavailable\",\"message\":\"%s circuit breaker is open\",\"service\":\"%s\"}",
                            serviceName.replace("Service", "-service"),
                            serviceName.replace("Service", "-service")));
        } catch (IOException e) {
            log.error("Failed to write circuit breaker error response", e);
        }
    }

    private void handleServiceError(HttpServletResponse response, String serviceName, Exception e) {
        log.warn("Service error after retries for {}: {}", serviceName, e.getMessage());
        try {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    String.format(
                            "{\"error\":\"Service Unavailable\",\"message\":\"%s is currently unavailable after retries\",\"service\":\"%s\"}",
                            serviceName.replace("Service", "-service"),
                            serviceName.replace("Service", "-service")));
        } catch (IOException ioException) {
            log.error("Failed to write service error response", ioException);
        }
    }

    private void handleUnexpectedError(HttpServletResponse response, String serviceName, Exception e) {
        log.error("Unexpected error in gateway for {}: {}", serviceName, e.getMessage(), e);
        try {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    String.format(
                            "{\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred\",\"service\":\"%s\"}",
                            serviceName.replace("Service", "-service")));
        } catch (IOException ioException) {
            log.error("Failed to write error response", ioException);
        }
    }

    private String getServiceName(String path) {
        if (path.startsWith("/api/customers"))
            return "customerService";
        if (path.startsWith("/api/products"))
            return "productService";
        if (path.startsWith("/api/orders"))
            return "orderService";
        if (path.startsWith("/api/payments"))
            return "paymentService";
        if (path.startsWith("/api/notifications"))
            return "notificationService";
        return null;
    }
}
