package com.burakpozut.api_gateway.filter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class TraceIdRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String> extreaHeaders = new LinkedHashMap<>();

    public TraceIdRequestWrapper(HttpServletRequest request, String headerName, String headerValue) {
        super(request);
        extreaHeaders.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String name) {
        String value = extreaHeaders.get(name);
        return value != null ? value : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = super.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, super.getHeader(name));
        }
        headers.putAll(extreaHeaders);
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (extreaHeaders.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(extreaHeaders.get(name)));
        }

        return super.getHeaders(name);
    }

}
