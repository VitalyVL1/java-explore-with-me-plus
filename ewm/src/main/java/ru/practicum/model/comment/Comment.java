package ru.practicum.model.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая комментарий к событию.
 * <p>
 * Содержит информацию о комментарии: автора, событие, текст, статус модерации
 * и время создания. Комментарии проходят модерацию перед публикацией.
 * </p>
 *
 * @see ru.practicum.model.event.Event
 * @see ru.practicum.model.user.User
 * @see CommentState
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Автор комментария.
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY) для оптимизации производительности.
     * Исключен из {@link ToString} для предотвращения циклических ссылок.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private User author;

    /**
     * Событие, к которому оставлен комментарий.
     * Связь многие-к-одному с сущностью {@link Event}.
     * Загружается лениво (LAZY) для оптимизации производительности.
     * Исключен из {@link ToString} для предотвращения циклических ссылок.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private Event event;

    /**
     * Текст комментария.
     * Не может быть null. Максимальная длина - 1000 символов (ограничение в БД).
     */
    @Column(nullable = false)
    private String text;

    /**
     * Статус модерации комментария {@link CommentState}.
     * Хранится в БД как строковое значение (EnumType.STRING).
     * Не может быть null.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private CommentState state;

    /**
     * Дата и время создания комментария.
     */
    @Column(name = "created_on")
    private LocalDateTime created;
}