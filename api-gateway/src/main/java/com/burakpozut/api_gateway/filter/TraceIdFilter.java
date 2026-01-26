package com.burakpozut.api_gateway.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(-1)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACEPARENT_HEADER = "traceparent";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = extractTraceId(request);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        HttpServletRequest wrapped = new TraceIdRequestWrapper(request, TRACE_ID_HEADER, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        filterChain.doFilter(wrapped, response);

    }

    private String extractTraceId(HttpServletRequest request) {
        String traceParent = request.getHeader(TRACEPARENT_HEADER);
        if (traceParent != null && traceParent.split("-").length >= 3) {
            return traceParent.split("-")[1];
        }

        return request.getHeader(TRACE_ID_HEADER);
    }

}
