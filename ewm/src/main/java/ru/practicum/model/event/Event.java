package ru.practicum.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая событие.
 * <p>
 * Содержит всю информацию о событии: основные данные (заголовок, описание),
 * временные метки (дата проведения, создания, публикации), настройки участия
 * (лимит участников, модерация заявок, платность), а также связи с инициатором
 * и категорией. Поля views и confirmedRequests являются транзиентными и
 * заполняются отдельными запросами к сервису статистики и репозиторию заявок.
 * </p>
 *
 * @see ru.practicum.model.user.User
 * @see ru.practicum.model.category.Category
 * @see Location
 * @see State
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {

    /**
     * Уникальный идентификатор события.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Инициатор события (пользователь, создавший событие).
     * Связь многие-к-одному с сущностью {@link User}.
     * Загружается лениво (LAZY) для оптимизации производительности.
     * Исключен из {@link ToString} для предотвращения циклических ссылок.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    private User initiator;

    /**
     * Категория события.
     * Связь многие-к-одному с сущностью {@link Category}.
     * Загружается лениво (LAZY) для оптимизации производительности.
     * Исключен из {@link ToString} для предотвращения циклических ссылок.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    /**
     * Заголовок события.
     * Максимальная длина - 120 символов, не может быть null.
     */
    @Column(name = "title", length = 120, nullable = false)
    private String title;

    /**
     * Краткое описание события.
     * Максимальная длина - 2000 символов, не может быть null.
     */
    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    /**
     * Полное описание события.
     * Максимальная длина - 7000 символов, не может быть null.
     */
    @Column(name = "description", length = 7000, nullable = false)
    private String description;

    /**
     * Статус события {@link State}.
     * Хранится в БД как строковое значение (EnumType.STRING).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20, nullable = false)
    private State state;

    /**
     * Местоположение события (широта и долгота).
     * Встраиваемый объект {@link Location}.
     */
    @Embedded
    private Location location;

    /**
     * Лимит участников события.
     * 0 означает отсутствие лимита.
     */
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    /**
     * Флаг необходимости модерации заявок на участие.
     * true - заявки требуют подтверждения инициатором,
     * false - заявки подтверждаются автоматически.
     */
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    /**
     * Флаг платности события.
     * true - участие платное, false - бесплатное.
     */
    @Column(name = "paid", nullable = false)
    private Boolean paid;

    /**
     * Дата и время проведения события.
     */
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    /**
     * Дата и время публикации события (после модерации).
     */
    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    /**
     * Дата и время создания события.
     * По умолчанию устанавливается текущее время.
     */
    @Column(name = "created_on", nullable = false)
    @Builder.Default
    private LocalDateTime createdOn = LocalDateTime.now();

    /**
     * Количество просмотров события.
     * Транзиентное поле, заполняется из сервиса статистики.
     */
    @Transient
    private Long views;

    /**
     * Количество подтвержденных заявок на участие.
     * Транзиентное поле, заполняется из репозитория заявок.
     */
    @Transient
    private Long confirmedRequests;
}