package com.example.adminservice.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class LogProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendLog(String service, String level, String message) {
        String logMsg = String.format("[%s] [%s] [%s]: %s", LocalDateTime.now(), service, level, message);
        rabbitTemplate.convertAndSend("log.queue", logMsg);
    }
}