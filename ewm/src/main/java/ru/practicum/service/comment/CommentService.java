package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long userId, NewCommentDto commentDto);

    List<CommentDto> getComments(long userId);

    CommentDto updateComment(long userId, UpdateCommentDto commentDto);

    void deleteComment(long userId, long comId);
}