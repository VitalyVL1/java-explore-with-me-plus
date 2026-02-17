package ru.practicum.model.user.mapper;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.user.User;

/**
 * Утилитарный класс-маппер для преобразования между сущностью пользователя и DTO.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link User}
 * в различные DTO в зависимости от уровня доступа, а также для создания
 * сущности из DTO создания.
 * </p>
 *
 * @see User
 * @see UserDto
 * @see UserShortDto
 * @see NewUserRequest
 */
public class UserMapper {

    /**
     * Преобразует полное DTO пользователя в сущность пользователя.
     *
     * @param userDto DTO пользователя с данными
     * @return сущность пользователя, готовую для сохранения в БД
     */
    public static User mapToUser(UserDto userDto) {
        return User.builder()
                .name(userDto.name())
                .email(userDto.email())
                .build();
    }

    /**
     * Преобразует DTO создания пользователя в сущность пользователя.
     *
     * @param userDto DTO с данными нового пользователя
     * @return сущность пользователя, готовую для сохранения в БД
     */
    public static User mapToUser(NewUserRequest userDto) {
        return User.builder()
                .name(userDto.name())
                .email(userDto.email())
                .build();
    }

    /**
     * Преобразует сущность пользователя в полное DTO пользователя.
     *
     * @param user сущность пользователя
     * @return DTO пользователя со всеми данными
     */
    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    /**
     * Преобразует сущность пользователя в краткое DTO пользователя.
     * <p>
     * Используется для встраивания в другие DTO (например, в EventShortDto, EventFullDto),
     * где требуется только идентификатор и имя пользователя.
     * </p>
     *
     * @param user сущность пользователя
     * @return DTO пользователя с краткой информацией (id, name)
     */
    public static UserShortDto mapToUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}