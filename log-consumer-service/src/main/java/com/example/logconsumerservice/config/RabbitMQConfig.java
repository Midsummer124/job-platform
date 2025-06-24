package com.example.logconsumerservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue logQueue() {
        return new Queue("log.queue", true); // durable queue
    }
}
