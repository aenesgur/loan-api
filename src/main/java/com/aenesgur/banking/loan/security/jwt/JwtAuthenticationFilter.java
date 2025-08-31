package com.aenesgur.banking.loan.security.jwt;

import com.aenesgur.banking.loan.exception.model.ErrorResponse;
import com.aenesgur.banking.loan.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            request.setAttribute("userId", jwtTokenProvider.getUserIdFromToken(token));

            String role = jwtTokenProvider.getRoleFromToken(token);
            if ("ROLE_CUSTOMER".equals(role)) {
                request.setAttribute("resolvedCustomerId", jwtTokenProvider.getCustomerIdFromToken(token));
            } else if ("ROLE_ADMIN".equals(role)) {
                String targetCustomerId = extractTargetCustomerId(request);
                if (targetCustomerId == null) {
                    sendErrorResponse(response, "The 'targetCustomerId' parameter is mandatory for admin authorization.");
                    return;                }
                request.setAttribute("resolvedCustomerId", targetCustomerId);
            }
        }
        else {
            sendErrorResponse(response, "Invalid or expired JWT.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(null)
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractTargetCustomerId(HttpServletRequest request) {
        String param = request.getParameter("targetCustomerId");
        if (StringUtils.hasText(param)) {
            return param;
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return pathMatcher.match("/api/*/auth/**", path) ||
                pathMatcher.match("/v3/api-docs/**", path) ||
                pathMatcher.match("/swagger-ui/**", path) ||
                pathMatcher.match("/api-docs/**", path) ||
                pathMatcher.match("/swagger-ui.html", path);
    }
}