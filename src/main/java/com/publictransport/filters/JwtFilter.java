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
public class JwtFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getRequestURI().startsWith(String.format("%s/api/secure", httpRequest.getContextPath())) == true) {
            String header = httpRequest.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
                return;
            }
            else {
                String token = header.substring(7);
                try {
                    String email = JwtUtils.validateTokenAndGetEmail(token);
                    if (email != null) {
                        httpRequest.setAttribute("email", email);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        chain.doFilter(request, response);
                        return;
                    }
                } catch (Exception e) {
                    // Log lỗi
                }
            }

            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token không hợp lệ hoặc hết hạn");
        }
        chain.doFilter(request, response);
    }

}