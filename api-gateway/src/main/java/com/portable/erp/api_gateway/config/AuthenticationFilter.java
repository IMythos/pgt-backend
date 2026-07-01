package com.portable.erp.api_gateway.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.portable.erp.api_gateway.infrastructure.security.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_POST_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/users"
        );

    private static final List<String> PUBLIC_GET_PREFIXES = List.of(
            "/api/v1/products",
            "/api/v1/brands",
            "/api/v1/categories",
            "/api/v1/kardex",
            "/api/v1/movimientos",
            "/api/v1/locations",
            "/api/v1/dashboard",
            "/api/v1/heatmap",
            "/api/v1/picking",
            "/ws"
        );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (isPublicPath(method, path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            return onError(exchange, "No se envió el header Authorization", HttpStatus.UNAUTHORIZED);
        }

        if (!authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Formato inválido: debe ser 'Bearer <token>'", HttpStatus.UNAUTHORIZED);
        }

        try {
            jwtUtil.validateToken(authHeader.substring(7));
        } catch (Exception e) {
            String reason = jwtUtil.getErrorReason(e);
            return onError(exchange, reason, HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(HttpMethod method, String path) {
        if (method == null)
            return false;
        if (HttpMethod.OPTIONS.equals(method))
            return true;

        String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        if (HttpMethod.POST.equals(method) && PUBLIC_POST_PATHS.contains(normalized)) {
            return true;
        }

        if (HttpMethod.GET.equals(method)) {
            return PUBLIC_GET_PREFIXES.stream().anyMatch(normalized::startsWith);
        }

        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                httpStatus.value(), httpStatus.getReasonPhrase(), message);

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
