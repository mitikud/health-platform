package com.medical.authservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class HeaderAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String userId = request.getHeader("X-User-Id");
        String role   = request.getHeader("X-User-Role"); // e.g., PATIENT/DOCTOR/ADMIN

        if (userId != null && !userId.isBlank()) {
            var authorities = (role == null || role.isBlank())
                    ? List.<SimpleGrantedAuthority>of()
                    : List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
            var auth = new UsernamePasswordAuthenticationToken(userId, "N/A", authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
