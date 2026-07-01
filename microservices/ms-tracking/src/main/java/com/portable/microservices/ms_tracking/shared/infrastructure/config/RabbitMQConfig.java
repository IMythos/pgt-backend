package com.portable.microservices.ms_tracking.shared.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.portable.microservices.ms_tracking.heatmap.infrastructure.messaging.dto.MovementCreatedMessage;

@Configuration
public class RabbitMQConfig {
    public static final String TRACKING_QUEUE = "tracking.heatmap.queue";
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String ROUTING_KEY = "inventory.movement.created";
    @Bean
    public Queue heatmapQueue() {
        return new Queue(TRACKING_QUEUE, true);
    }

    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(INVENTORY_EXCHANGE);
    }

    @Bean
    public Binding bindingHeatmap(Queue heatmapQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(heatmapQueue).to(inventoryExchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper mapper = new DefaultJackson2JavaTypeMapper();
        mapper.setTrustedPackages("*");
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.portable.microservices.ms_inventory.movement.domain.event.MovementCreatedEvent", MovementCreatedMessage.class);
        mapper.setIdClassMapping(idClassMapping);
        converter.setJavaTypeMapper(mapper);
        return converter;
    }
}