package com.burakpozut.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class GatewayLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(GatewayLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullUri = queryString == null ? uri : uri + "?" + queryString;
            int status = response.getStatus();

            String sourceService = getSourceService(request);

            logger.info("Gateway Request: {} {} -> Status: {} ({}ms) | Source: {}",
                    method, fullUri, status, duration, sourceService);

        }
    }

    private String getSourceService(HttpServletRequest request) {
        String sourceService = request.getHeader("X-Source-Service");
        if (sourceService != null) {
            return sourceService;
        }

        // Check User-Agent as fallback (for browser requests)
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.contains("Mozilla")) {
            return "Browser";
        }

        // Check Referer header
        String referer = request.getHeader("Referer");
        if (referer != null) {
            return "External";
        }

        // Default for inter-service calls without header
        return "Unknown";
    }
}