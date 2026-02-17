package ru.practicum.model.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запрос (заявку) на участие в событии.
 * <p>
 * Содержит информацию о заявке пользователя на участие в событии:
 * кто подал заявку (requester), на какое событие (event), статус заявки
 * и время создания. Заявки используются для управления участием пользователей
 * в событиях с ограниченным количеством мест.
 * </p>
 *
 * @see ru.practicum.model.user.User
 * @see ru.practicum.model.event.Event
 * @see RequestStatus
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    /**
     * Уникальный идентификатор заявки.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Пользователь, подавший заявку.
     * Связь многие-к-одному с сущностью {@link User}.
     */
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    /**
     * Событие, на которое подана заявка.
     * Связь многие-к-одному с сущностью {@link Event}.
     */
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Статус заявки {@link RequestStatus}.
     * Хранится в БД как строковое значение (EnumType.STRING).
     * Определяет текущее состояние заявки (ожидает, подтверждена, отклонена, отменена).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    /**
     * Дата и время создания заявки.
     */
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
}