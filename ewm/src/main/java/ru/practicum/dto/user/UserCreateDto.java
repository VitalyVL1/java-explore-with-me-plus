package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Имя не может быть пустым")
        @Email(message = "Email должен соответствовать своему формату")
        @Size(max = 100, message = "Имя не может быть больше 100 символов")
        String email,
        @NotBlank(message = "Email не может быть пустым")
        @Size(max = 254, message = "Email не может быть больше 254 символов")
        String name
) {
}