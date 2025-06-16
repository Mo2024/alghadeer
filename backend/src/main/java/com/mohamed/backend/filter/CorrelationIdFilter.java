package com.mohamed.backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String correlationId = httpRequest.getHeader("X-Correlation-ID");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            MDC.put("cid", correlationId);
            MDC.put("appName", "alghadeer");
            MDC.put("thread", Thread.currentThread().getName());

            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Clean up after request
        }
    }
}
