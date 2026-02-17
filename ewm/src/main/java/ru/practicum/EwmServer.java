package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс запуска основного сервиса Explore With Me (EWM).
 * <p>
 * Отвечает за инициализацию и запуск Spring Boot приложения для основного сервиса,
 * который реализует всю бизнес-логику платформы: управление пользователями,
 * событиями, категориями, подборками, заявками на участие и комментариями.
 * </p>
 *
 * @see org.springframework.boot.SpringApplication
 * @see SpringBootApplication
 */
@SpringBootApplication
@Slf4j
public class EwmServer {

    /**
     * Точка входа в приложение.
     * <p>
     * Запускает Spring Boot приложение с конфигурацией из класса {@link EwmServer}.
     * При запуске логирует информацию о старте приложения.
     * </p>
     *
     * @param args аргументы командной строки, передаваемые при запуске
     */
    public static void main(String[] args) {
        log.info("Запуск основного сервиса EWM (SpringApplication.run(EwmServer.class, args))");
        SpringApplication.run(EwmServer.class, args);
    }
}