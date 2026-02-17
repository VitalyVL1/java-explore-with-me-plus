package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.practicum.model.event.Location;

import java.time.LocalDateTime;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для создания нового события.
 * <p>
 * Используется при передаче данных от клиента к серверу для создания нового события.
 * Содержит все необходимые поля с валидацией и значениями по умолчанию.
 * </p>
 *
 * @param annotation краткое описание события (от 20 до 2000 символов)
 * @param category идентификатор категории события
 * @param description полное описание события (от 20 до 7000 символов)
 * @param eventDate дата и время проведения события (должна быть в будущем)
 * @param location местоположение события (широта и долгота)
 * @param paid флаг платности события (по умолчанию false)
 * @param participantLimit лимит участников (0 - без лимита, по умолчанию 0)
 * @param requestModeration флаг необходимости модерации заявок (по умолчанию true)
 * @param title заголовок события (от 3 до 120 символов)
 */
public record NewEventDto(
        @NotBlank(message = "Аннотация не может быть пустой")
        @Size(min = 20, max = 2000, message = "Аннотация должна быть не менее 20 и не более 2000 символов")
        String annotation,

        @NotNull(message = "Категория должна быть заполнена")
        Long category,

        @NotBlank(message = "Описание не может быть пустым")
        @Size(min = 20, max = 7000, message = "Описание должно быть не менее 20 и не более 7000 символов")
        String description,

        @NotNull(message = "Дата события должна быть указана")
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        @Future(message = "Дата события должна быть в будущем")
        LocalDateTime eventDate,

        @NotNull(message = "Место проведения события должно быть указано")
        Location location,

        Boolean paid,
        @PositiveOrZero(message = "Лимит участников должен быть неотрицательным")
        Integer participantLimit,
        Boolean requestModeration,

        @NotBlank(message = "Название должно быть указано")
        @Size(min = 3, max = 120, message = "Название должно быть не менее 3 и не более 120 символов")
        String title
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok и значениями по умолчанию.
     * <p>
     * Устанавливает:
     * - paid = false, если не указано
     * - participantLimit = 0, если не указано
     * - requestModeration = true, если не указано
     * </p>
     */
    @Builder(toBuilder = true)
    public NewEventDto {
        paid = paid != null ? paid : false;
        participantLimit = participantLimit != null ? participantLimit : 0;
        requestModeration = requestModeration != null ? requestModeration : true;
    }
}