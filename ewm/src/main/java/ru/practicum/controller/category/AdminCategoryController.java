package ru.practicum.controller.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

/**
 * REST-контроллер для административного управления категориями событий.
 * <p>
 * Предоставляет endpoints для создания, обновления и удаления категорий.
 * Доступен только пользователям с ролью администратора.
 * </p>
 *
 * @see CategoryService
 * @see CategoryDto
 */
@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    /**
     * Создает новую категорию.
     * <p>
     * Эндпоинт для добавления новой категории в систему. После успешного создания
     * категория становится доступной для использования в событиях.
     * </p>
     *
     * @param category DTO создаваемой категории с названием
     * @return DTO созданной категории с присвоенным идентификатором
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto category) {
        log.info("Admin: Method launched (save(CategoryDto category = {}))", category);
        return categoryService.save(category);
    }

    /**
     * Обновляет существующую категорию.
     * <p>
     * Позволяет администратору изменить название категории по её идентификатору.
     * </p>
     *
     * @param categoryId идентификатор обновляемой категории (должен быть положительным)
     * @param category   DTO с новыми данными категории
     * @return DTO обновленной категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws jakarta.validation.ConstraintViolationException если categoryId не положительный
     */
    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable @Positive Long categoryId,
            @Valid @RequestBody CategoryDto category
    ) {
        log.info("Admin: Method launched (update(Long categoryId = {}, CategoryDto category = {}))", categoryId, category);
        return categoryService.update(categoryId, category);
    }

    /**
     * Удаляет категорию.
     * <p>
     * Удаляет категорию по её идентификатору. Категория не может быть удалена,
     * если она используется в каких-либо событиях.
     * </p>
     *
     * @param categoryId идентификатор удаляемой категории (должен быть положительным)
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws jakarta.validation.ConstraintViolationException если categoryId не положительный
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long categoryId) {
        log.info("Admin: Method launched (delete(Long categoryId = {}))", categoryId);
        categoryService.delete(categoryId);
    }
}