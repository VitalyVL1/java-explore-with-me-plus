package ru.practicum.service.user;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * <p>
 * Определяет бизнес-логику для операций с пользователями от имени администратора:
 * создание новых пользователей, получение списка с фильтрацией и пагинацией,
 * удаление пользователей.
 * </p>
 *
 * @see ru.practicum.controller.UserController
 * @see UserDto
 * @see NewUserRequest
 * @see UserParam
 */
public interface UserService {

    /**
     * Сохраняет нового пользователя.
     *
     * @param user DTO с данными нового пользователя (email и имя)
     * @return DTO созданного пользователя с присвоенным идентификатором
     * @throws org.springframework.dao.DataIntegrityViolationException если пользователь с таким email уже существует
     */
    UserDto save(NewUserRequest user);

    /**
     * Возвращает список пользователей с возможностью фильтрации и пагинации.
     *
     * @param userParam параметры фильтрации (список идентификаторов) и пагинации (from, size)
     * @return список DTO пользователей, отсортированных по идентификатору
     */
    List<UserDto> findAll(UserParam userParam);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор удаляемого пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    void deleteById(Long userId);
}