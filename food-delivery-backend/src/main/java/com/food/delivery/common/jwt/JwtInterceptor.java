package com.food.delivery.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.delivery.common.response.Result;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        // 放行登录、注册、H2 控制台等
        if (uri.startsWith("/user/login")
                || uri.startsWith("/user/register")
                || uri.startsWith("/h2-console")
                || uri.startsWith("/actuator")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response);
            return false;
        }

        try {
            Claims claims = JwtUtil.parse(token);
            LoginUser loginUser = new LoginUser();
            loginUser.setId(claims.get("uid", Long.class));
            loginUser.setRole(claims.get("role", String.class));
            LoginUser.set(loginUser);
            return true;
        } catch (Exception e) {
            writeUnauthorized(response);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LoginUser.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = MAPPER.writeValueAsString(Result.unauthorized());
        response.getWriter().write(body);
    }
}
