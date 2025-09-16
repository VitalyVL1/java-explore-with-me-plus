package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class EwmServer {
    public static void main(String[] args) {
        log.info("Method launched (SpringApplication.run(EwmServer.class, args))");
        ConfigurableApplicationContext context = SpringApplication.run(EwmServer.class, args);
    }
}