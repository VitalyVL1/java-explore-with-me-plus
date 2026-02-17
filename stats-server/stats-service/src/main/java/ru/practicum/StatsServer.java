package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс запуска сервиса статистики (Stats Server).
 * <p>
 * Отвечает за инициализацию и запуск Spring Boot приложения для сервиса статистики,
 * который собирает и предоставляет данные о просмотрах событий. Сервис принимает
 * информацию о каждом запросе к эндпоинтам (хиты) и позволяет получать
 * агрегированную статистику за различные периоды с фильтрацией.
 * </p>
 *
 * @see org.springframework.boot.SpringApplication
 * @see SpringBootApplication
 */
@SpringBootApplication
@Slf4j
public class StatsServer {

    /**
     * Точка входа в приложение.
     * <p>
     * Запускает Spring Boot приложение с конфигурацией из класса {@link StatsServer}.
     * При запуске логирует информацию о старте приложения.
     * </p>
     *
     * @param args аргументы командной строки, передаваемые при запуске
     */
    public static void main(String[] args) {
        log.info("Запуск сервиса статистики (SpringApplication.run(StatsServer.class, args))");
        SpringApplication.run(StatsServer.class, args);
    }
}