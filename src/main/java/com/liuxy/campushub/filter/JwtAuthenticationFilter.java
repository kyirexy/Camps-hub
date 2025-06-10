package com.liuxy.campushub.filter;

import com.liuxy.campushub.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        
        // 只对 OPTIONS 请求直接放行
        if (request.getMethod().equals("OPTIONS")) {
            logger.debug("OPTIONS request, passing through");
            filterChain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", requestTokenHeader);

        // 如果没有token，直接放行
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwtToken = requestTokenHeader.substring(7);
        if (jwtToken == null || jwtToken.isBlank() || "null".equalsIgnoreCase(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        Long userId = null;
            logger.debug("JWT Token: {}", jwtToken);
            try {
                var claims = jwtTokenUtil.parseToken(jwtToken);
                username = claims.get("username", String.class);
                userId = claims.get("userId", Long.class);
                logger.debug("Parsed username: {}, userId: {}", username, userId);
            } catch (IllegalArgumentException | ExpiredJwtException e) {
                logger.error("JWT Token解析错误", e);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Loading user details for username: {}", username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken)) {
                logger.debug("Token is valid, setting authentication");
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                
                // 将用户ID添加到请求属性中
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    logger.debug("Added userId to request attributes: {}", userId);
                }
            } else {
                logger.debug("Token validation failed");
            }
        } else {
            logger.debug("No username found or authentication already exists");
        }
        
        filterChain.doFilter(request, response);
    }
}