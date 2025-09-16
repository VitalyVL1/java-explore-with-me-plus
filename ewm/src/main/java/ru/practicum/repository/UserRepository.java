package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    Page<User> findByIdIn(List<Long> ids, Pageable pageable);
}