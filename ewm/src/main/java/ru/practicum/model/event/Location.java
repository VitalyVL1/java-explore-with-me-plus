package ru.practicum.model.event;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Встраиваемый объект, представляющий географическое местоположение события.
 * <p>
 * Содержит координаты места проведения события: широту и долготу.
 * Используется как встраиваемый компонент в сущности {@link Event}.
 * </p>
 *
 * @see Event
 * @see jakarta.persistence.Embedded
 */
@Getter
@Setter
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    /**
     * Географическая широта места проведения события.
     * Не может быть null.
     */
    @Column(name = "lat", nullable = false)
    private Float lat;

    /**
     * Географическая долгота места проведения события.
     * Не может быть null.
     */
    @Column(name = "lon", nullable = false)
    private Float lon;
}