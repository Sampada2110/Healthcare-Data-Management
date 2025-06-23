package com.hcdm.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = Optional.ofNullable(request.getHeader("X-Request-ID"))
                .orElse(UUID.randomUUID().toString());

        response.setHeader("X-Request-ID", requestId);
        MDC.put("X-Request-ID", requestId); // For logging
        filterChain.doFilter(request, response);
        MDC.remove("X-Request-ID");
    }
}
