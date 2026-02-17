package ru.practicum.controller.category;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryParam;
import ru.practicum.service.category.CategoryService;

import java.util.List;

/**
 * REST-контроллер для публичного доступа к категориям событий.
 * <p>
 * Предоставляет endpoints для получения списка всех категорий с пагинацией
 * и получения информации о конкретной категории по её идентификатору.
 * Доступен всем пользователям без необходимости аутентификации.
 * </p>
 *
 * @see CategoryService
 * @see CategoryDto
 * @see CategoryParam
 */
@RestController
@RequestMapping("/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Возвращает список всех категорий с поддержкой пагинации.
     * <p>
     * Позволяет получить все доступные категории событий с возможностью
     * постраничного вывода. Результат отсортирован по идентификатору категории.
     * </p>
     *
     * @param from индекс первого элемента для пагинации (по умолчанию 0, должен быть неотрицательным)
     * @param size количество элементов на странице (по умолчанию 10, должен быть положительным)
     * @return список DTO категорий
     * @throws jakarta.validation.ConstraintViolationException если параметры пагинации не соответствуют ограничениям
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("Public: Method launched (findAll(Integer from = {}, Integer size = {}))", from, size);
        return categoryService.findAll(new CategoryParam(from, size));
    }

    /**
     * Возвращает информацию о категории по её идентификатору.
     * <p>
     * Позволяет получить детальную информацию о конкретной категории событий.
     * </p>
     *
     * @param categoryId идентификатор категории (должен быть положительным)
     * @return DTO категории с указанным идентификатором
     * @throws ru.practicum.exception.NotFoundException        если категория с указанным ID не найдена
     * @throws jakarta.validation.ConstraintViolationException если categoryId не положительный
     */
    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        log.info("Public: Method launched (findById(Long categoryId = {}))", categoryId);
        return categoryService.findById(categoryId);
    }
}