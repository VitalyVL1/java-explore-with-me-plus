package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.service.user.UserService;

import java.util.List;

/**
 * REST-контроллер для административного управления пользователями.
 * <p>
 * Предоставляет endpoints для создания, просмотра и удаления пользователей.
 * Доступен только пользователям с ролью администратора.
 * </p>
 *
 * @see UserService
 * @see UserDto
 * @see NewUserRequest
 * @see UserParam
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    /**
     * Возвращает список пользователей с возможностью фильтрации и пагинации.
     * <p>
     * Позволяет администратору получить список пользователей, опционально фильтруя
     * по списку идентификаторов, с поддержкой постраничного вывода.
     * </p>
     *
     * @param ids список идентификаторов пользователей для фильтрации (необязательный параметр)
     * @param from индекс первого элемента для пагинации (по умолчанию 0, должен быть неотрицательным)
     * @param size количество элементов на странице (по умолчанию 10, должен быть положительным)
     * @return список DTO пользователей
     * @throws jakarta.validation.ConstraintViolationException если параметры пагинации не соответствуют ограничениям
     */
    @GetMapping("/users")
    public List<UserDto> findAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Method launched (findAll(List<Long> ids = {}, Integer from = {}, Integer size = {}))", ids, from,
                size);
        UserParam userParam = new UserParam(ids, from, size);
        return userService.findAll(userParam);
    }

    /**
     * Создает нового пользователя.
     * <p>
     * Позволяет администратору зарегистрировать нового пользователя в системе.
     * </p>
     *
     * @param user DTO с данными нового пользователя (email и имя)
     * @return DTO созданного пользователя с присвоенным идентификатором
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody @Valid NewUserRequest user) {
        log.info("Method launched (save(UserCreateDto user = {}))", user);
        return userService.save(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * <p>
     * Позволяет администратору удалить пользователя из системы.
     * При удалении пользователя также удаляются все связанные с ним данные
     * (события, комментарии, заявки) благодаря каскадному удалению в БД.
     * </p>
     *
     * @param userId идентификатор удаляемого пользователя (должен быть положительным)
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @Positive Long userId) {
        log.info("Method launched (deleteById(Long userId = {}))", userId);
        userService.deleteById(userId);
    }
}