package ru.practicum.service.user;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.QUser;
import ru.practicum.model.User;
import ru.practicum.model.mapper.UserMapper;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public UserDto save(UserDto user) {
        User savedUser = userRepository.save(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> findAll(UserParam userParam) {
        QUser user = QUser.user;

        JPAQuery<User> query = queryFactory.selectFrom(user);

        if (userParam.ids() != null && !userParam.ids().isEmpty()) {
            query.where(user.id.in(userParam.ids()));
        }

        query.orderBy(user.id.asc());

        List<User> users = query
                .offset(userParam.from())
                .limit(userParam.size())
                .fetch();

        return users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя " +
                " id = " + userId));
        userRepository.deleteById(userId);
    }
}