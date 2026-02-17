package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) для создания нового пользователя.
 * <p>
 * Используется при передаче данных от клиента к серверу для регистрации нового пользователя.
 * Содержит обязательные поля: email и имя пользователя с соответствующими валидациями.
 * </p>
 *
 * @param email электронная почта пользователя (обязательное поле, должна соответствовать формату email,
 *              длина от 6 до 254 символов)
 * @param name имя пользователя (обязательное поле, длина от 2 до 250 символов)
 */
public record NewUserRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Email должен соответствовать своему формату")
        @Size(min = 6, max = 254, message = "Email не может быть больше 254 символов")
        String email,

        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 250, message = "Имя не может быть больше 250 символов")
        String name
) {
}