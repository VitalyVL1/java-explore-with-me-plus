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
 * DTO (Data Transfer Object) для обновления события администратором.
 * <p>
 * Используется при передаче данных от администратора к серверу для модерации
 * и редактирования события. Все поля являются опциональными - обновляются только
 * те поля, которые указаны в запросе. Содержит специальные действия для
 * публикации или отклонения события.
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
 * @param stateAction действие над событием: PUBLISH_EVENT (опубликовать) или REJECT_EVENT (отклонить)
 * @param title новый заголовок события (от 3 до 120 символов)
 */
public record UpdateEventAdminRequest(
        @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
        String description,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        @Future(message = "Дата начала события должна быть в будущем")
        LocalDateTime eventDate,

        Location location,

        Boolean paid,

        @PositiveOrZero(message = "Лимит пользователей должен быть положительным числом")
        Integer participantLimit,

        Boolean requestModeration,

        StateAction stateAction,

        @Size(min = 3, max = 120, message = "Длина названия должна быть от 3 до 120 символов")
        String title
) {
    /**
     * Конструктор с параметром по умолчанию для stateAction.
     * Устанавливает stateAction = NO_ACTION, если не передан.
     */
    public UpdateEventAdminRequest {
        stateAction = stateAction != null ? stateAction : StateAction.NO_ACTION;
    }

    /**
     * Валидирует допустимость действия администратора.
     * <p>
     * Проверяет, что запрашиваемое действие является одним из допустимых для администратора:
     * NO_ACTION (без действия), PUBLISH_EVENT (опубликовать) или REJECT_EVENT (отклонить).
     * Действия SEND_TO_REVIEW и CANCEL_REVIEW недопустимы для администратора.
     * </p>
     *
     * @return true если действие является NO_ACTION, PUBLISH_EVENT или REJECT_EVENT
     */
    @AssertTrue(message = "Администратор может использовать только PUBLISH_EVENT или REJECT_EVENT")
    public boolean isValidStateAction() {
        return stateAction == StateAction.NO_ACTION ||
               stateAction == StateAction.PUBLISH_EVENT ||
               stateAction == StateAction.REJECT_EVENT;
    }
}