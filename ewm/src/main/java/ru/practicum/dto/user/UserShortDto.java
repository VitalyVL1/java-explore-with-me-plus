package ru.practicum.dto.user;

/**
 * DTO (Data Transfer Object) для краткого представления пользователя.
 * <p>
 * Используется для передачи основной информации о пользователе в составах других DTO
 * (например, в {@link ru.practicum.dto.event.EventShortDto} или {@link ru.practicum.dto.event.EventFullDto}).
 * Содержит только идентификатор и имя пользователя без контактных данных.
 * </p>
 *
 * @param id уникальный идентификатор пользователя
 * @param name имя пользователя
 */
public record UserShortDto(Long id, String name) {
}