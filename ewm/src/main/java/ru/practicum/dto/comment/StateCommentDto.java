package ru.practicum.dto.comment;

import ru.practicum.model.comment.CommentState;

/**
 * DTO (Data Transfer Object) для представления комментария администратору.
 * <p>
 * Используется для передачи информации о комментарии в административном доступе.
 * В отличие от публичного {@link CommentDto}, содержит информацию о статусе модерации комментария.
 * </p>
 *
 * @param id уникальный идентификатор комментария
 * @param author имя автора комментария
 * @param text текст комментария
 * @param state статус модерации комментария {@link CommentState}
 * @see CommentState
 * @see CommentDto
 */
public record StateCommentDto(
        Long id,
        String author,
        String text,
        CommentState state
) {
}