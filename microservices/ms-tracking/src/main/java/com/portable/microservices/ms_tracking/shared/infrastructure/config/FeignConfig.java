package com.portable.microservices.ms_tracking.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor bearerTokenForwardInterceptor() {
        return requestTemplate -> {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes sra) {
                String auth = sra.getRequest().getHeader("Authorization");
                if (auth != null && auth.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", auth);
                }
            }
        };
    }
}
