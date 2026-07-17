package com.example.demo.security;

import com.example.demo.exception.HandleJwtException;
import com.example.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;



/**
 * JWT 인증 필터
 *
 * 모든 HTTP 요청에 대해 JWT 토큰을 검증하고 인증 정보를 SecurityContext에 설정합니다.
 *
 * 동작 흐름:
 * 1. Authorization 헤더에서 JWT 토큰 추출
 * 2. JWT 토큰 유효성 검증
 * 3. 토큰이 유효하면 사용자 정보를 로드하여 SecurityContext에 인증 정보 설정
 * 4. 다음 필터로 요청 전달
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = resolveJwt(request);                                   // 1. Authorization 헤더에서 JWT 토큰 추출
            if (jwt != null && jwtUtility.validateJwt(jwt)) {                   // 2. JWT가 존재하고 유효한 경우
                Authentication auth = getAuthentication(jwt);                   // 3. JWT에서 사용자 정보를 추출하여 Authentication 객체 생성
                SecurityContextHolder.getContext().setAuthentication(auth);     // 4. SecurityContext에 인증 정보 설정
            }
            filterChain.doFilter(request, response);                            // 5. 다음 필터로 요청 전달


        } catch (HandleJwtException e) {
            // JWT 검증 실패 시 401 Unauthorized 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("error: " + e.getMessage());
        }
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출
     *
     * @param request HTTP 요청
     * @return JWT 토큰 (Bearer 접두사 제거), 없으면 null
     */
    private String resolveJwt(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 null 반환
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        // "Bearer " 접두사 제거 후 토큰 반환
        return authorizationHeader.substring(7);
    }

    /**
     * JWT에서 사용자 정보를 추출하여 Authentication 객체 생성
     *
     * @param jwt JWT 토큰
     * @return Authentication 객체
     */
    private Authentication getAuthentication(String jwt) {
        // JWT의 subject(userId)를 이용해 사용자 정보 로드
        String userId = jwtUtility.getClaimsFromJwt(jwt).getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

        // UsernamePasswordAuthenticationToken 생성
        // 파라미터: principal(사용자 정보), credentials(자격 증명), authorities(권한)
        return new UsernamePasswordAuthenticationToken(
                userDetails, // principal: 인증된 사용자 정보
                jwt, // credentials: JWT 토큰
                userDetails.getAuthorities() // authorities: 사용자 권한 목록
        );
    }
}
