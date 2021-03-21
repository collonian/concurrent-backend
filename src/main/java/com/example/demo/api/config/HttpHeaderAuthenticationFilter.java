package com.example.demo.api.config;

import com.example.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

public class HttpHeaderAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;

    private final String headerKey = "X-USER-ID";
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain filterChain
    ) throws ServletException, IOException {
        String userId = request.getHeader(headerKey);
        if(null != userId && SecurityContextHolder.getContext().getAuthentication() == null) {
            userService.findByUserId(new BigDecimal(userId)).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            });
        }

        if(filterChain == null) {
            return;
        }
        filterChain.doFilter(request, response);
    }
}
