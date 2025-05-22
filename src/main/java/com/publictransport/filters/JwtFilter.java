package com.publictransport.filters;

import com.publictransport.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter implements Filter {
    private final JwtUtils jwtUtils;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Check if the request is for a secure API
        if (requestURI.startsWith(String.format("%s/api/secure", httpRequest.getContextPath()))) {
            String authorizationHeader = httpRequest.getHeader("Authorization");

            // Validate the Authorization header
            if (isInvalidAuthorizationHeader(authorizationHeader)) {
                sendUnauthorizedError(response, "Missing or invalid Authorization header.");
                return;
            }

            String token = authorizationHeader.substring(7); // Extract token
            try {
                String email = jwtUtils.validateTokenAndGetEmail(token);
                if (email != null) {
                    setAuthentication(email);
                    chain.doFilter(request, response); // Proceed with the request
                    return;
                }
            } catch (Exception e) {
                // Log the error (optional)
            }

            sendUnauthorizedError(response, "Invalid or expired token.");
            return;
        }

        chain.doFilter(request, response); // Proceed for non-secure requests
    }

    private boolean isInvalidAuthorizationHeader(String header) {
        return header == null || !header.startsWith("Bearer ");
    }

    private void sendUnauthorizedError(ServletResponse response, String message) throws IOException {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private void setAuthentication(String email) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
