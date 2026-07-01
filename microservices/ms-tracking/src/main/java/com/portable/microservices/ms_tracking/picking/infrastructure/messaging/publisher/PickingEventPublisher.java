package com.portable.microservices.ms_tracking.picking.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.portable.microservices.ms_tracking.picking.infrastructure.config.PickingRabbitMQConfig;
import com.portable.microservices.ms_tracking.picking.infrastructure.messaging.dto.PickingCompletedMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(PickingCompletedMessage message) {
        log.info("Publicando evento de picking completado. Orden ID: {}", message.ordenId());
        rabbitTemplate.convertAndSend(
                PickingRabbitMQConfig.TRACKING_EXCHANGE,
                PickingRabbitMQConfig.ROUTING_KEY,
                message
        );
    }
}
