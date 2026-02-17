package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) для обновления существующего комментария.
 * <p>
 * Используется при передаче данных от клиента к серверу для редактирования комментария.
 * Содержит идентификатор редактируемого комментария и новый текст.
 * </p>
 *
 * @param id уникальный идентификатор редактируемого комментария (обязательное поле, должно быть положительным)
 * @param text новый текст комментария (обязательное поле, длина от 1 до 1000 символов)
 */
public record UpdateCommentDto(
        @NotNull(message = "ID комментария не может быть null")
        @Positive(message = "ID комментария должно быть больше 0")
        Long id,

        @NotBlank(message = "Комментарий не может быть пустым")
        @Size(max = 1000, min = 1, message = "Комментарий должен быть не менее 1 и не более 1000 символов")
        String text
) {
}