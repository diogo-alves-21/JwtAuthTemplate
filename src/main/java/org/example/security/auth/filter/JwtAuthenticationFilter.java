package org.example.security.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.example.security.auth.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Log4j2
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;


    public JwtAuthenticationFilter (JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver,
                                    UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        log.debug("Incoming request {} {} | Authorization header={}", request.getMethod(), request.getRequestURI(), authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token present, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.debug("JWT detected, preparing to validate (length={})", jwt.length());

            final String userEmail = jwtService.extractUsername(jwt);
            log.debug("Extracted username={} from JWT", userEmail);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("JWT is valid for user {}", userEmail);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            log.error("Failed to authenticate JWT for request {} {}: {}", request.getMethod(), request.getRequestURI(), exception.getMessage(), exception);
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
