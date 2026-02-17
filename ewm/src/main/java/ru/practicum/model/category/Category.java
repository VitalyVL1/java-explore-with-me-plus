package ru.practicum.model.category;

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
 * Сущность, представляющая категорию события.
 * <p>
 * Содержит информацию о категории, к которой может относиться событие.
 * Категории используются для классификации событий по тематикам.
 * </p>
 *
 * @see ru.practicum.model.event.Event
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    /**
     * Уникальный идентификатор категории.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название категории.
     * Не может быть null и должно содержать до 50 символов.
     * Должно быть уникальным.
     */
    @Column(nullable = false, length = 50)
    private String name;
}