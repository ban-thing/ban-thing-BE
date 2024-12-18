package com.example.banthing.global.security;

import com.example.banthing.global.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromHeader(request);

        if (StringUtils.hasText(token)) {
            log.info(token);

            String checkToken = jwtUtil.validateToken(token);
            if (!checkToken.equals("")) {
                log.error(checkToken);
                setTokenError(response, checkToken);
                return;
            }

            String userId = jwtUtil.getUserInfoFromToken(token).getSubject();
            log.info("userId : " + userId);

            try {
                setAuthentication(userId);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(userId);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, null);
    }

    private void setTokenError(HttpServletResponse response, String message) {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(401);

        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(ApiResponse.tokenErrorResponse(message)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
