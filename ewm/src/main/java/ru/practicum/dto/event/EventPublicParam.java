package ru.practicum.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.event.EventSort;

import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для параметров публичного поиска событий.
 * <p>
 * Используется для передачи параметров фильтрации, сортировки и пагинации
 * при публичном запросе списка событий. Содержит значения по умолчанию
 * для параметров пагинации и фильтра onlyAvailable, а также встроенную валидацию
 * диапазона дат.
 * </p>
 *
 * @param text текст для поиска в аннотации и описании события
 * @param categories множество идентификаторов категорий для фильтрации
 * @param paid флаг платности события
 * @param rangeStart начало диапазона дат событий
 * @param rangeEnd конец диапазона дат событий
 * @param onlyAvailable фильтр только по доступным событиям (с неподтвержденными заявками)
 * @param sort тип сортировки (по дате или по количеству просмотров)
 * @param from количество элементов для пропуска (пагинация)
 * @param size количество элементов на странице (пагинация)
 */
public record EventPublicParam(
        String text,
        Set<Long> categories,
        Boolean paid,

        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime rangeStart,

        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime rangeEnd,

        Boolean onlyAvailable,
        EventSort sort,

        @PositiveOrZero(message = "Параметр from должен быть неотрицательным")
        Integer from,

        @Positive(message = "Параметр size должен быть больше нуля")
        Integer size
) {
    /**
     * Конструктор с параметрами по умолчанию.
     * <p>
     * Устанавливает:
     * - from = 0, если не передан
     * - size = 10, если не передан
     * - onlyAvailable = false, если не передан
     * </p>
     */
    public EventPublicParam {
        from = from != null ? from : 0;
        size = size != null ? size : 10;
        onlyAvailable = onlyAvailable != null ? onlyAvailable : false;
    }

    /**
     * Валидирует корректность диапазона дат.
     * <p>
     * Проверяет, что если указаны обе даты, то rangeEnd следует после rangeStart.
     * </p>
     *
     * @return true если диапазон дат корректен или одна из дат не указана
     */
    @AssertTrue(message = "rangeEnd должен быть после rangeStart")
    public boolean isRangeValid() {
        return rangeStart == null || rangeEnd == null || rangeEnd.isAfter(rangeStart);
    }
}