package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) для создания нового комментария.
 * <p>
 * Используется при передаче данных от клиента к серверу для создания нового комментария.
 * Содержит обязательные поля: идентификатор события и текст комментария.
 * </p>
 *
 * @param event идентификатор события, к которому оставляется комментарий (обязательное поле, должно быть положительным)
 * @param text текст комментария (обязательное поле, длина от 1 до 1000 символов)
 */
public record NewCommentDto(
        @NotNull(message = "ID события не может быть null")
        @Positive(message = "ID события должно быть больше 0")
        Long event,

        @NotBlank(message = "Текст комментария не может быть пустым")
        @Size(max = 1000, min = 1, message = "Комментарий должен быть не менее 1 и не более 1000 символов")
        String text
) {
}