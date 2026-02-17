package ru.practicum.model.compilation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import ru.practicum.model.event.Event;

/**
 * Сущность, представляющая связь между подборкой и событием (промежуточная таблица).
 * <p>
 * Реализует отношение многие-ко-многим между {@link Compilation} и {@link Event}.
 * Позволяет одной подборке содержать множество событий, а одному событию
 * входить в множество подборок.
 * </p>
 *
 * @see Compilation
 * @see Event
 * @see ru.practicum.repository.CompilationEventRepository
 */
@Entity
@Table(name = "compilation_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEvent {

    /**
     * Уникальный идентификатор записи связи.
     * Генерируется автоматически базой данных при сохранении.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Подборка, с которой связана запись.
     * Связь многие-к-одному с сущностью {@link Compilation}.
     */
    @ManyToOne
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;

    /**
     * Событие, с которым связана запись.
     * Связь многие-к-одному с сущностью {@link Event}.
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}