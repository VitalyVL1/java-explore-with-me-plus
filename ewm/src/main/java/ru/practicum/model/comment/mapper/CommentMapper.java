package ru.practicum.model.comment.mapper;

import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.CommentState;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import java.time.LocalDateTime;

/**
 * Утилитарный класс-маппер для преобразования между сущностью комментария и DTO.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link Comment}
 * в различные DTO в зависимости от уровня доступа, а также для создания
 * сущности из DTO создания.
 * </p>
 *
 * @see Comment
 * @see CommentDto
 * @see StateCommentDto
 * @see NewCommentDto
 */
public class CommentMapper {

    /**
     * Преобразует DTO создания комментария в сущность комментария.
     * <p>
     * Создает объект {@link Comment} на основе данных из DTO,
     * добавляя автора, событие, статус и текущее время создания.
     * </p>
     *
     * @param commentDto DTO с данными нового комментария
     * @param author автор комментария
     * @param event событие, к которому оставляется комментарий
     * @param state статус комментария (обычно WAITING для новых)
     * @return сущность комментария, готовую для сохранения в БД
     */
    public static Comment mapToComment(NewCommentDto commentDto, User author, Event event, CommentState state) {
        return Comment.builder()
                .author(author)
                .event(event)
                .text(commentDto.text())
                .state(state)
                .created(LocalDateTime.now())
                .build();
    }

    /**
     * Преобразует сущность комментария в DTO для публичного доступа.
     * <p>
     * Создает объект {@link CommentDto}, содержащий только основную информацию
     * о комментарии (без статуса модерации).
     * </p>
     *
     * @param comment сущность комментария
     * @return DTO комментария для публичного доступа
     */
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getText());
    }

    /**
     * Преобразует сущность комментария в DTO для административного доступа.
     * <p>
     * Создает объект {@link StateCommentDto}, содержащий полную информацию
     * о комментарии, включая статус модерации.
     * </p>
     *
     * @param comment сущность комментария
     * @return DTO комментария для администратора
     */
    public static StateCommentDto mapToAdminDto(Comment comment) {
        return new StateCommentDto(comment.getId(), comment.getAuthor().getName(), comment.getText(), comment.getState());
    }
}