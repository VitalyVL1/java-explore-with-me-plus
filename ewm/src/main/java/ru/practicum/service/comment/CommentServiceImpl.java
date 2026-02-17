package ru.practicum.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.exception.AccessDeniedException;
import ru.practicum.exception.CommentStateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.comment.DateSort;
import ru.practicum.model.comment.mapper.CommentMapper;
import ru.practicum.model.comment.CommentState;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса для управления комментариями.
 * <p>
 * Обеспечивает бизнес-логику для операций с комментариями: создание, обновление,
 * удаление, получение списков, модерацию. Поддерживает различные уровни доступа
 * (пользовательский, административный, публичный) и фильтрацию по статусам.
 * </p>
 *
 * @see CommentService
 * @see CommentRepository
 * @see CommentMapper
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Возвращает все комментарии указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO комментариев пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public List<CommentDto> getComments(long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<Comment> comments = commentRepository.findAllByAuthor(author);

        return comments.stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    /**
     * Создает новый комментарий.
     * <p>
     * Комментарий создается со статусом {@link CommentState#WAITING}.
     * </p>
     *
     * @param userId идентификатор автора комментария
     * @param commentDto DTO с данными нового комментария
     * @return DTO созданного комментария
     * @throws NotFoundException если пользователь или событие не найдены
     */
    @Override
    @Transactional
    public CommentDto createComment(long userId, NewCommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findById(commentDto.event())
                .orElseThrow(() -> new NotFoundException("Событие с id " + commentDto.event() + " не найдено"));

        Comment comment = commentRepository.save(CommentMapper.mapToComment(commentDto, author, event, CommentState.WAITING));
        return CommentMapper.mapToCommentDto(comment);
    }

    /**
     * Обновляет существующий комментарий.
     * <p>
     * После обновления статус комментария сбрасывается на {@link CommentState#WAITING}
     * для повторной модерации.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param commentDto DTO с обновленными данными комментария
     * @return DTO обновленного комментария
     * @throws NotFoundException если пользователь или комментарий не найдены
     * @throws AccessDeniedException если пользователь не является автором комментария
     */
    @Override
    @Transactional
    public CommentDto updateComment(long userId, UpdateCommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Comment comment = commentRepository.findById(commentDto.id())
                .orElseThrow(() -> new NotFoundException("Комментария с id " + commentDto.id() + " не найдено"));

        if (comment.getAuthor() != author) {
            throw new AccessDeniedException("Редактировать может только автор комментария");
        }
        comment.setText(commentDto.text());
        return CommentMapper.mapToCommentDto(comment);
    }

    /**
     * Удаляет комментарий пользователя.
     *
     * @param userId идентификатор пользователя
     * @param comId идентификатор комментария
     * @throws NotFoundException если комментарий не найден
     * @throws AccessDeniedException если пользователь не является автором комментария
     */
    @Override
    @Transactional
    public void deleteComment(long userId, long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));
        if (comment.getAuthor().getId() == userId) {
            commentRepository.delete(comment);
        } else {
            throw new AccessDeniedException("Удалять комментарий может только автор");
        }
    }

    /**
     * Возвращает все комментарии с фильтрацией по тексту и сортировкой.
     * <p>
     * Метод для административного доступа.
     * </p>
     *
     * @param text текст для поиска (опционально)
     * @param sort направление сортировки по дате
     * @return список DTO комментариев для администратора
     */
    @Override
    public List<StateCommentDto> getComments(String text, DateSort sort) {
        Iterable<Comment> comments = commentRepository.findAll(CommentRepository.Predicate.textFilter(text), getSortDate(sort));
        return StreamSupport.stream(comments.spliterator(), false)
                .map(CommentMapper::mapToAdminDto)
                .toList();
    }

    /**
     * Проводит модерацию комментария.
     *
     * @param comId идентификатор комментария
     * @param approved true - подтвердить комментарий, false - отклонить
     * @return DTO комментария с обновленным статусом
     * @throws NotFoundException если комментарий не найден
     * @throws CommentStateException если комментарий не в статусе WAITING
     */
    @Override
    @Transactional
    public StateCommentDto reviewComment(long comId, boolean approved) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));

        if (!comment.getState().equals(CommentState.WAITING)) {
            throw new CommentStateException("Подтверждение комментария может осуществляться только если статус равен WAITING");
        }

        if (approved) {
            comment.setState(CommentState.APPROVED);
        } else {
            comment.setState(CommentState.REJECTED);
        }

        return CommentMapper.mapToAdminDto(comment);
    }

    /**
     * Удаляет комментарий (административный доступ).
     *
     * @param comId идентификатор комментария
     * @throws NotFoundException если комментарий не найден
     */
    @Override
    @Transactional
    public void deleteComment(long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new NotFoundException("Комментария с id " + comId + " не найдено"));
        commentRepository.delete(comment);
    }

    /**
     * Возвращает комментарии с указанным статусом.
     * <p>
     * Метод для публичного доступа (только APPROVED комментарии).
     * </p>
     *
     * @param state статус комментария
     * @param sort направление сортировки по дате
     * @return список DTO комментариев
     */
    @Override
    public List<CommentDto> getCommentsByState(CommentState state, DateSort sort) {
        Iterable<Comment> comments = commentRepository.findAll(CommentRepository.Predicate.stateFilter(state), getSortDate(sort));
        return StreamSupport.stream(comments.spliterator(), false)
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    /**
     * Возвращает комментарии к указанному событию.
     * <p>
     * Метод для публичного доступа.
     * </p>
     *
     * @param eventId идентификатор события
     * @param sort направление сортировки по дате
     * @return список DTO комментариев
     */
    @Override
    public List<CommentDto> getCommentsByEvent(long eventId, DateSort sort) {
        Iterable<Comment> comments = commentRepository.findAll(CommentRepository.Predicate.eventFilter(eventId), getSortDate(sort));
        return StreamSupport.stream(comments.spliterator(), false)
                .map(CommentMapper::mapToCommentDto)
                .toList();
    }

    /**
     * Создает объект сортировки Spring Data на основе переданного направления.
     *
     * @param sort направление сортировки
     * @return объект {@link Sort} для использования в запросах
     */
    private Sort getSortDate(DateSort sort) {
        return (sort == DateSort.DESC) ?
                Sort.by("created").descending() : Sort.by("created").ascending();
    }
}