package ru.practicum.dto.comment;

/**
 * DTO (Data Transfer Object) для публичного представления комментария.
 * <p>
 * Используется для передачи информации о комментарии клиенту в публичном доступе.
 * Содержит только основную информацию без статуса модерации.
 * </p>
 *
 * @param id уникальный идентификатор комментария
 * @param author имя автора комментария
 * @param text текст комментария (прошедший модерацию)
 */
public record CommentDto(
        Long id,
        String author,
        String text
) {
}