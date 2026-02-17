package ru.practicum.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность, представляющая пользователя системы.
 * <p>
 * Содержит основную информацию о пользователе: идентификатор, имя и email.
 * Пользователи могут создавать события, оставлять комментарии, подавать заявки на участие.
 * Email является уникальным идентификатором пользователя в системе.
 * </p>
 *
 * @see ru.practicum.model.event.Event
 * @see ru.practicum.model.comment.Comment
 * @see ru.practicum.model.request.Request
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Уникальный идентификатор пользователя.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Имя пользователя.
     * Не может быть null, максимальная длина - 100 символов.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Email пользователя.
     * Не может быть null, максимальная длина - 254 символа.
     * Должен быть уникальным в системе.
     */
    @Column(nullable = false, length = 254, unique = true)
    private String email;
}