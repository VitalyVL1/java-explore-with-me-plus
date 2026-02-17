package ru.practicum.model.compilation;

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
 * Сущность, представляющая подборку событий.
 * <p>
 * Подборка - это тематическая группа событий, которая может быть закреплена
 * на главной странице. Содержит заголовок и флаг закрепления.
 * Связь с событиями реализована через отдельную таблицу {@link CompilationEvent}.
 * </p>
 *
 * @see ru.practicum.model.compilation.CompilationEvent
 * @see ru.practicum.model.event.Event
 */
@Entity
@Table(name = "compilations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {

    /**
     * Уникальный идентификатор подборки.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Заголовок подборки.
     * Отображается в интерфейсе как название подборки.
     */
    private String title;

    /**
     * Флаг закрепления подборки на главной странице.
     * true - подборка отображается в закрепленном виде,
     * false - подборка не закреплена.
     */
    private Boolean pinned;
}