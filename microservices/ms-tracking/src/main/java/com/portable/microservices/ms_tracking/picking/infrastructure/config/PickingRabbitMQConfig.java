package com.portable.microservices.ms_tracking.picking.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PickingRabbitMQConfig {

    public static final String TRACKING_EXCHANGE = "tracking.exchange";
    public static final String ROUTING_KEY = "tracking.picking.completed";

    @Bean
    public TopicExchange trackingExchange() {
        return new TopicExchange(TRACKING_EXCHANGE);
    }
}