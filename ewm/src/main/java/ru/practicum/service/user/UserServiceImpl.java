package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.user.User;
import ru.practicum.model.user.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Реализация сервиса для управления пользователями.
 * <p>
 * Обеспечивает бизнес-логику для операций с пользователями: создание,
 * получение списка с фильтрацией и пагинацией, удаление. Использует
 * спецификации QueryDSL для динамической фильтрации.
 * </p>
 *
 * @see UserService
 * @see UserRepository
 * @see UserMapper
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Сохраняет нового пользователя.
     *
     * @param user DTO с данными нового пользователя (email, имя)
     * @return DTO созданного пользователя с присвоенным идентификатором
     * @throws org.springframework.dao.DataIntegrityViolationException если пользователь с таким email уже существует
     */
    @Override
    @Transactional
    public UserDto save(NewUserRequest user) {
        User savedUser = userRepository.save(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(savedUser);
    }

    /**
     * Возвращает список пользователей с фильтрацией и пагинацией.
     * <p>
     * Если передан список идентификаторов, выполняется фильтрация по ним.
     * Результат сортируется по идентификатору и применяется пагинация.
     * </p>
     *
     * @param userParam параметры фильтрации (список ID) и пагинации (from, size)
     * @return список DTO пользователей
     */
    @Override
    public List<UserDto> findAll(UserParam userParam) {
        Iterable<User> users = userParam.ids() != null && !userParam.ids().isEmpty() ?
                userRepository.findAll(UserRepository.Predicate.byIds(userParam.ids())) : userRepository.findAll();

        return StreamSupport.stream(users.spliterator(), false)
                .sorted(Comparator.comparing(User::getId))
                .skip(userParam.from())
                .limit(userParam.size())
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор удаляемого пользователя
     * @throws NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    @Transactional
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }
}