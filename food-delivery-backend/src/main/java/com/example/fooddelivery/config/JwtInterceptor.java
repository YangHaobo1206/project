package com.example.fooddelivery.config;

import com.example.fooddelivery.common.BizException;
import com.example.fooddelivery.mapper.UserMapper;
import com.example.fooddelivery.security.AuthContext;
import com.example.fooddelivery.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            throw new BizException(401, "Unauthorized");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtil.parse(token);
        Long userId = Long.valueOf(claims.getSubject());
        String role = (String) claims.get("role");
        if (userMapper.selectById(userId) == null) {
            throw new BizException(401, "Invalid user");
        }
        AuthContext.set(new AuthContext.UserSession(userId, role));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
