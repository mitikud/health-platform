package com.medical.apigateway.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Value("${jwt.public-key:}")
    private String publicKeyPem;

    @Value("#{'${security.permit:/api/auth/**,/actuator/**}'.split(',')}")
    private List<String> permitPatterns;

    private volatile PublicKey cachedKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var path = exchange.getRequest().getURI().getPath();
        if (permitPatterns.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            if (cachedKey == null) cachedKey = jwtService.toRsaPublicKeyFromPem(publicKeyPem);
            var jws = jwtService.parse(auth.substring(7), cachedKey);
            Claims claims = jws.getBody();
            String userId = jwtService.getSubject(claims);
            String role   = jwtService.getRole(claims);

            // propagate identity to downstream services
            var mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role == null ? "" : role)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override public int getOrder() { return -100; } // run early
}