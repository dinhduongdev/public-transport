package com.publictransport.filters;

import com.publictransport.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
        String authorizationHeader = httpRequest.getHeader("Authorization");

        // Các API yêu cầu chứng thực
        if (requestURI.startsWith(httpRequest.getContextPath() + "/api/admin-only") ||
                requestURI.startsWith(httpRequest.getContextPath() + "/api/secure") ||
                requestURI.startsWith(httpRequest.getContextPath() + "/api/favorites") ||
                requestURI.startsWith(httpRequest.getContextPath() + "/api/notifications") ) {

            if (isInvalidAuthorizationHeader(authorizationHeader)) {
                sendUnauthorizedError(response, "Missing or invalid Authorization header.");
                return;
            }
            String token = authorizationHeader.substring(7);
            try {
                String email = jwtUtils.validateTokenAndGetEmail(token);
                String role = jwtUtils.getRoleFromToken(token);

                if (email != null && role != null) {
                    // Nếu là admin-only thì kiểm tra thêm role
                    if (requestURI.startsWith(httpRequest.getContextPath() + "/api/admin-only")
                            && !"ADMIN".equals(role)) {
                        sendUnauthorizedError(response, "Admin access only.");
                        return;
                    }
                    setAuthentication(email, role);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                // log optional
            }
            sendUnauthorizedError(response, "Invalid or expired token.");
            return;
        }
        // Không yêu cầu chứng thực
        chain.doFilter(request, response);
    }

    private boolean isInvalidAuthorizationHeader(String header) {
        return header == null || !header.startsWith("Bearer ");
    }

    private void sendUnauthorizedError(ServletResponse response, String message) throws IOException {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private void setAuthentication(String email, String role) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
