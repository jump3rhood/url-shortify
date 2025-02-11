package org.john.personal.urlshortify.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.john.personal.urlshortify.models.User;
import org.john.personal.urlshortify.repositories.UserRepository;
import org.john.personal.urlshortify.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ResponseStatusException {
        log.info("=== Interceptor Start ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Method: {}", request.getMethod());
        log.info("=== Interceptor End ===");

        // skip authentication for login. sign up end points
        if(request.getRequestURI().contains("/api/auth/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            Optional<Claims> optionalClaims = jwtUtil.validateTokenAndGetClaims(token);

            if(optionalClaims.isPresent()){
                Claims claims = optionalClaims.get();
                String email = claims.getSubject();
                Optional<User> optionalUser = userRepository.findByEmail(email);

                if(optionalUser.isPresent()){
                    User currentUser = optionalUser.get();
                    // add user info to request attrs
                    request.setAttribute("user", currentUser);
                    return true;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or missing token");
    }
}
