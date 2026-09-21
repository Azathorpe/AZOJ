package org.example.azoi.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 没带 token，放行（由具体接口决定是否需要登录）
            return true;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validate(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":-1,\"msg\":\"token 无效\"}");
            return false;
        }

        Long userId = jwtUtil.getUserId(token);
        // 把 userId 放到 request 属性里，Controller 可以取
        request.setAttribute("userId", userId);
        return true;
    }
}