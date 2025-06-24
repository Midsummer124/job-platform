package com.example.logconsumerservice.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

@Component
public class LogConsumer {

    @Value("${log.file-path}")
    private String logFilePath;

    @RabbitListener(queues = "log.queue")
    public void receiveLog(String logMessage) {
        try (FileWriter fw = new FileWriter(Paths.get(logFilePath).toFile(), true)) {
            fw.write(logMessage + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
