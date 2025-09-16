package ru.practicum.service.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.QUser;
import ru.practicum.model.User;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto save(UserCreateDto user) {
        User savedUser = userRepository.save(UserMapper.mapToUser(user));
        return UserMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> findAll(UserParam userParam) {
        QUser user = QUser.user;

        BooleanExpression predicate = userParam.ids() != null && !userParam.ids().isEmpty()
                ? user.id.in(userParam.ids())
                : null;

        if (userParam.from() != null && userParam.size() != null) {
            Pageable pageable = PageRequest.of(
                    userParam.from() / userParam.size(),
                    userParam.size()
            );

            Page<User> userPage = predicate != null
                    ? userRepository.findAll(predicate, pageable)
                    : userRepository.findAll(pageable);

            return userPage.getContent().stream()
                    .map(UserMapper::mapToUserResponseDto)
                    .collect(Collectors.toList());
        }

        List<User> users = predicate != null
                ? (List<User>) userRepository.findAll(predicate)
                : userRepository.findAll();

        return users.stream()
                .map(UserMapper::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя " +
                " id = " + userId));
        userRepository.deleteById(userId);
    }
}