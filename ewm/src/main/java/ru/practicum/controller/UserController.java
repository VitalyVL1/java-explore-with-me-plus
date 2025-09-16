package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserParam;
import ru.practicum.dto.user.UserResponseDto;
import ru.practicum.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDto> findAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) @Positive Integer from,
            @RequestParam(required = false) @Positive Integer size
    ) {
        log.info("Method launched (findAll(List<Long> ids = {}, Integer from = {}, Integer size = {}))", ids, from, size);
        UserParam userParam = new UserParam(ids, from, size);
        return userService.findAll(userParam);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto save(@RequestBody @Valid UserCreateDto user) {
        log.info("Method launched (save(UserCreateDto user = {}))", user);
        return userService.save(user);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @Positive Long userId) {
        log.info("Method launched (deleteById(Long userId = {}))", userId);
        userService.deleteById(userId);
    }
}