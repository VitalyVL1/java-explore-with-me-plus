package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * DTO (Data Transfer Object) для обновления существующей подборки событий.
 * <p>
 * Используется при передаче данных от клиента к серверу для обновления подборки.
 * Все поля являются опциональными - обновляются только те поля, которые указаны в запросе.
 * </p>
 *
 * @see CompilationDto
 */
@Data
@Builder
public class UpdateCompilationRequest {
    /**
     * Флаг закрепления подборки на главной странице.
     * Если указан, обновляет признак закрепления подборки.
     */
    private Boolean pinned;

    /**
     * Новый заголовок подборки.
     * Если указан, должен содержать от 1 до 50 символов.
     */
    @Size(min = 1, max = 50, message = "Заголовок должен быть не менее 1 и не более 50 символов")
    private String title;

    /**
     * Новое множество идентификаторов событий, входящих в подборку.
     * Аннотация {@link JsonProperty} используется для корректной десериализации
     * из JSON с именем поля "events".
     * Если указан, полностью заменяет список событий в подборке.
     */
    @JsonProperty("events")
    private Set<Long> eventIds;
}