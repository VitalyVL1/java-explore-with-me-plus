package ru.practicum.service.user;

import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto save(UserCreateDto user);

    List<UserResponseDto> findAll(UserParam userParam);

    void deleteById(Long userId);
}