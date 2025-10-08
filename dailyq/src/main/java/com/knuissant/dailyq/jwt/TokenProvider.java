package com.knuissant.dailyq.jwt;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.domain.users.User;
import com.knuissant.dailyq.config.JwtProperties;

/**
 * JWT 토큰 생성, 검증 및 관련 작업을 처리하는 서비스
 * Access Token과 Refresh Token의 생성 및 검증을 담당
 */
@Slf4j
@Service
public class TokenProvider {

    private final Key key;                              // 토큰 서명에 사용되는 키
    private final long accessTokenExpirationMillis;     // 액세스 토큰 만료 시간
    private final long refreshTokenExpirationMillis;    // 리프레시 토큰 만료 시간
    private final JwtParser jwtParser;                  // 재사용할 JwtParser 필드

    /**
     * TokenProvider 생성자
     */
    public TokenProvider(JwtProperties jwtProperties) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMillis = jwtProperties.accessTokenExpirationMillis();
        this.refreshTokenExpirationMillis = jwtProperties.refreshTokenExpirationMillis();
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    /**
     * 사용자 정보를 기반으로 액세스 토큰을 생성
     * 토큰에는 사용자 ID, 이메일, 역할 정보가 포함됨
     *
     * @param user 토큰을 발급할 사용자 정보
     * @return 생성된 JWT 액세스 토큰
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", "ROLE_" + user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 사용자 정보를 기반으로 리프레시 토큰을 생성
     * 보안을 위해 최소한의 정보만 포함 (사용자 ID만 포함)
     *
     * @param user 토큰을 발급할 사용자 정보
     * @return 생성된 JWT 리프레시 토큰
     */
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 액세스 토큰으로부터 Authentication 객체를 생성
     * Spring Security에서 인증 정보로 사용됨
     *
     * @param accessToken 유효한 액세스 토큰
     * @return 인증 정보가 담긴 Authentication 객체
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        Set<SimpleGrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority(claims.get("role").toString()));

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
    }

    /**
     * 토큰에서 사용자 ID를 추출
     *
     * @param token JWT 토큰
     * @return 토큰에서 추출한 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    /**
     * 토큰의 유효성을 검사
     * 만료 여부, 서명 검증 등을 수행
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 클레임 정보를 추출
    private Claims parseClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도 클레임 정보는 필요할 수 있으므로, 예외에서 클레임을 추출하여 반환합니다.
            return e.getClaims();
        }
    }
}
