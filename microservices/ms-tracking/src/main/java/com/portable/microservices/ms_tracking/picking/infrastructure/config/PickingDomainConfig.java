package com.portable.microservices.ms_tracking.picking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.portable.microservices.ms_tracking.picking.domain.service.ConstructorGrafoService;

@Configuration
public class PickingDomainConfig {

    @Bean
    public ConstructorGrafoService constructorGrafoService() {
        return new ConstructorGrafoService();
    }
}