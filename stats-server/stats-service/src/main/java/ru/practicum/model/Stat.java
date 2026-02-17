package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись статистики о просмотре (хите) эндпоинта.
 * <p>
 * Содержит информацию о каждом запросе к API: идентификатор приложения,
 * URI эндпоинта, IP-адрес пользователя и временную метку запроса.
 * Используется для сбора и анализа статистики посещаемости.
 * </p>
 *
 * @see ru.practicum.dto.HitCreateDto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stats")
public class Stat {

    /**
     * Уникальный идентификатор записи статистики.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор приложения, отправившего запрос.
     * Не может быть null, максимальная длина - 50 символов.
     */
    @Column(name = "app", nullable = false, length = 50)
    private String app;

    /**
     * URI эндпоинта, к которому был совершен запрос.
     * Не может быть null, максимальная длина - 1000 символов.
     */
    @Column(name = "uri", nullable = false, length = 1000)
    private String uri;

    /**
     * IP-адрес пользователя, совершившего запрос.
     * Не может быть null, максимальная длина - 15 символов (IPv4).
     */
    @Column(name = "ip", nullable = false, length = 15)
    private String ip;

    /**
     * Дата и время совершения запроса.
     * Не может быть null.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}