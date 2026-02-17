package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.StateAction;

import java.time.LocalDateTime;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для обновления события пользователем.
 * <p>
 * Используется при передаче данных от авторизованного пользователя к серверу
 * для редактирования своего события. Все поля являются опциональными - обновляются
 * только те поля, которые указаны в запросе. Содержит специальные действия для
 * отправки события на модерацию или отмены.
 * </p>
 *
 * @param annotation краткое описание события (от 20 до 2000 символов)
 * @param category идентификатор новой категории события
 * @param description полное описание события (от 20 до 7000 символов)
 * @param eventDate новая дата и время проведения события (должна быть в будущем)
 * @param location новое местоположение события
 * @param paid новый флаг платности события
 * @param participantLimit новый лимит участников
 * @param requestModeration новый флаг необходимости модерации заявок
 * @param stateAction действие над событием: SEND_TO_REVIEW (отправить на модерацию) или CANCEL_REVIEW (отменить)
 * @param title новый заголовок события (от 3 до 120 символов)
 */
public record UpdateEventUserRequest(
        @Size(min = 20, max = 2000, message = "Аннотация должна быть не менее 20 и не более 2000 символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Описание должно быть не менее 20 и не более 7000 символов")
        String description,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        @Future(message = "Дата события должна быть в будущем")
        LocalDateTime eventDate,

        Location location,
        Boolean paid,

        @PositiveOrZero(message = "Лимит участников должен быть неотрицательным")
        Integer participantLimit,
        Boolean requestModeration,
        StateAction stateAction,

        @Size(min = 3, max = 120, message = "Название должно быть не менее 3 и не более 120 символов")
        String title
) {
    /**
     * Конструктор с параметром по умолчанию для stateAction.
     * Устанавливает stateAction = NO_ACTION, если не передан.
     */
    public UpdateEventUserRequest {
        stateAction = stateAction != null ? stateAction : StateAction.NO_ACTION;
    }

    /**
     * Валидирует допустимость действия пользователя.
     * <p>
     * Проверяет, что запрашиваемое действие является одним из допустимых для пользователя:
     * NO_ACTION (без действия), SEND_TO_REVIEW (отправить на модерацию) или CANCEL_REVIEW (отменить).
     * Действия PUBLISH_EVENT и REJECT_EVENT недопустимы для пользователя.
     * </p>
     *
     * @return true если действие является NO_ACTION, SEND_TO_REVIEW или CANCEL_REVIEW
     */
    @AssertTrue(message = "Пользователь может использовать только SEND_TO_REVIEW или CANCEL_REVIEW")
    public boolean isValidStateAction() {
        return stateAction == StateAction.NO_ACTION ||
               stateAction == StateAction.SEND_TO_REVIEW ||
               stateAction == StateAction.CANCEL_REVIEW;
    }
}