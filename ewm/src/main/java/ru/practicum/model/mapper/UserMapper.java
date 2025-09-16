package ru.practicum.model.mapper;

import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.model.User;

public class UserMapper {
    public static User mapToUser(UserCreateDto userCreateDto) {
        return User.builder().name(userCreateDto.name()).email(userCreateDto.email()).build();
    }

    public static UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(user.getEmail(), user.getId(), user.getName());
    }
}