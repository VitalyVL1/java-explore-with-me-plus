package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * DTO (Data Transfer Object) для создания новой подборки событий.
 * <p>
 * Используется при передаче данных от клиента к серверу для создания новой подборки.
 * Содержит информацию о заголовке, признаке закрепления и списке событий.
 * </p>
 *
 * @see CompilationDto
 */
@Data
@Builder
public class NewCompilationDto {
    /**
     * Флаг закрепления подборки на главной странице.
     * true - подборка будет закреплена, false - не закреплена.
     * Если не указан, по умолчанию будет установлено false.
     */
    private Boolean pinned;

    /**
     * Заголовок подборки.
     * Обязательное поле, должно содержать от 1 до 50 символов.
     */
    @Size(min = 1, max = 50, message = "Заголовок должен быть не менее 1 и не более 50 символов")
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    /**
     * Множество идентификаторов событий, которые войдут в подборку.
     * Аннотация {@link JsonProperty} используется для корректной десериализации
     * из JSON с именем поля "events".
     */
    @JsonProperty("events")
    private Set<Long> eventIds;
}