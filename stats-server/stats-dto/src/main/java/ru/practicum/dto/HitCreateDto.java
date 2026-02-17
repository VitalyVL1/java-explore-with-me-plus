package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для создания записи о просмотре (хита) в сервисе статистики.
 * <p>
 * Используется при передаче данных от клиента к серверу для сохранения информации
 * о каждом обращении к эндпоинту. Содержит данные о приложении, URI, IP-адресе
 * и временной метке запроса.
 * </p>
 *
 * @param app идентификатор приложения, отправившего запрос (не может быть пустым)
 * @param uri URI эндпоинта, к которому был совершен запрос (не может быть пустым)
 * @param ip IP-адрес пользователя, совершившего запрос (не может быть пустым)
 * @param timestamp дата и время совершения запроса (должна быть в прошлом или настоящем)
 */
public record HitCreateDto(
        @NotBlank(message = "Идентификатор приложения не может быть пустым")
        String app,

        @NotBlank(message = "URI не может быть пустым")
        String uri,

        @NotBlank(message = "IP-адрес не может быть пустым")
        String ip,

        @NotNull(message = "Временная метка не может быть null")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @PastOrPresent(message = "Дата и время совершения запроса к эндпоинту должна быть не позже текущей даты и времени")
        LocalDateTime timestamp
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать объекты с использованием toBuilder().
     */
    @Builder(toBuilder = true)
    public HitCreateDto {
    }
}