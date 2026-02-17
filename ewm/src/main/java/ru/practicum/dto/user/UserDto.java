package ru.practicum.dto.user;

/**
 * DTO (Data Transfer Object) для представления пользователя.
 * <p>
 * Используется для передачи информации о пользователе между клиентом и сервером.
 * Содержит основные данные пользователя: идентификатор, email и имя.
 * </p>
 *
 * @param email электронная почта пользователя
 * @param id уникальный идентификатор пользователя
 * @param name имя пользователя
 */
public record UserDto(
        String email,
        Long id,
        String name
) {
}