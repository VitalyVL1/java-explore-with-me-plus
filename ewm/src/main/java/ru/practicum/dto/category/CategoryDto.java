package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) для передачи данных категории.
 * <p>
 * Используется для обмена информацией о категориях между клиентом и сервером.
 * Представляет собой неизменяемый объект (record) с полями идентификатора и названия категории.
 * </p>
 *
 * @param id уникальный идентификатор категории (заполняется сервером при создании)
 * @param name название категории, не может быть пустым и должно содержать от 1 до 50 символов
 */
public record CategoryDto(
        Long id,

        @NotBlank(message = "Название категории не может быть пустым")
        @Size(min = 1, max = 50, message = "Название категории не может быть больше 50 символов")
        String name
) {
}