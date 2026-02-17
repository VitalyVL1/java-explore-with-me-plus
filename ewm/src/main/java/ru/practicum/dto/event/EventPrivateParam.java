package ru.practicum.dto.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO (Data Transfer Object) для параметров пагинации при запросе событий пользователя.
 * <p>
 * Используется для передачи параметров пагинации в сервисный слой при получении
 * списка событий конкретного пользователя. Содержит значения по умолчанию
 * для параметров пагинации.
 * </p>
 *
 * @param from количество элементов, которое необходимо пропустить для формирования текущей страницы
 *             (должен быть неотрицательным, по умолчанию 0)
 * @param size количество элементов для отображения на одной странице
 *             (должен быть положительным, по умолчанию 10)
 */
public record EventPrivateParam(
        @PositiveOrZero(message = "Параметр from должен быть неотрицательным")
        Integer from,
        @Positive(message = "Параметр size должен быть больше нуля")
        Integer size
) {
    /**
     * Конструктор с параметрами по умолчанию для пагинации.
     * Устанавливает значения from=0 и size=10, если они не были переданы.
     */
    public EventPrivateParam {
        from = from != null ? from : 0;
        size = size != null ? size : 10;
    }
}