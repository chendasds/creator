/*
 * @Author: dingxiuchen 2745250790@qq.com
 * @Date: 2026-03-19 11:16:21
 * @LastEditors: dingxiuchen 2745250790@qq.com
 * @LastEditTime: 2026-03-19 15:14:54
 * @FilePath: \build-one\src\main\java\com\creation\platform\interceptor\JwtInterceptor.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.creation.platform.interceptor;

import com.creation.platform.entity.Result;
import com.creation.platform.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String RENEWAL_TOKEN_HEADER = "Renewal-Token";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        // 设置响应编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        // 放行 OPTIONS 请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 Token
        String token = extractToken(request);
        if (token == null || token.isEmpty()) {
            writeUnauthorizedResponse(response, "未提供 Token，请先登录");
            return false;
        }

        // 校验 Token
        if (!JwtUtils.validateToken(token)) {
            writeUnauthorizedResponse(response, "Token 已过期或无效，请重新登录");
            return false;
        }

        System.out.println("【拦截器监控】当前请求路径：" + request.getRequestURI());

        // 【核心续期逻辑】检查是否需要续期（剩余时间不足 30 分钟）
        Long userId = JwtUtils.getUserId(token);
        if (userId != null) {
            if (JwtUtils.needsRenewal(token)) {
                System.out.println("【拦截器监控】发现 Token 快过期！准备签发新 Token...");

                // 生成新的 Token
                String newToken = JwtUtils.generateToken(userId);
                // 将新 Token 放入响应 Header
                response.setHeader(RENEWAL_TOKEN_HEADER, newToken);

                // 显式暴露该 Header，允许前端 Axios 读取
                response.setHeader("Access-Control-Expose-Headers", RENEWAL_TOKEN_HEADER);
                System.out.println("【拦截器监控】已签发新 Token：" + newToken);
            } else {
                System.out.println("【拦截器监控】Token 无需续期");
            }

            // 将用户 ID 存入请求属性，供后续 Controller 使用
            request.setAttribute("userId", userId);
        }

        return true;
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 写入 401 响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        Result<?> result = Result.error(401, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
