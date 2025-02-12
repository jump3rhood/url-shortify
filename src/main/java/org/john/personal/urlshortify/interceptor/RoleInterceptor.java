package org.john.personal.urlshortify.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.john.personal.urlshortify.models.Role;
import org.john.personal.urlshortify.security.annotation.RequireRole;
import org.john.personal.urlshortify.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AccessDeniedException {
        // if it's not a HTTP handler method
        if(!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requiredRole = handlerMethod.getMethod().getAnnotation(RequireRole.class);

        if(requiredRole == null) return true;

        String token = request.getHeader("Authorization").substring(7);
        if(!jwtUtil.hasRole(token, requiredRole.value())){
            throw new AccessDeniedException("Insufficient Privileges");
        }

        return true;
    }


}
