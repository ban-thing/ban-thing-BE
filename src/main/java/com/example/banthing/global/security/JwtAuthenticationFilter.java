package com.example.banthing.global.security;

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

        String token = jwtUtil.getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {

            // JWT 토큰 substring
            log.info(token);

            if (!jwtUtil.validateToken(token)) {
                log.error("Token Error");
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

        // 다음 필터로 요청 전달
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
}
