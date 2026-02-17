package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryParam;

import java.util.List;

/**
 * Сервис для управления категориями событий.
 * <p>
 * Определяет бизнес-логику для работы с категориями: создание, обновление,
 * удаление, получение по идентификатору и получение списка с пагинацией.
 * </p>
 *
 * @see ru.practicum.controller.category.AdminCategoryController
 * @see ru.practicum.controller.category.PublicCategoryController
 * @see CategoryDto
 * @see CategoryParam
 */
public interface CategoryService {

    /**
     * Сохраняет новую категорию.
     *
     * @param category DTO с данными категории (название)
     * @return DTO созданной категории с присвоенным идентификатором
     * @throws ru.practicum.exception.AlreadyExistsException если категория с таким названием уже существует
     */
    CategoryDto save(CategoryDto category);

    /**
     * Удаляет категорию по идентификатору.
     *
     * @param id идентификатор удаляемой категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConditionsNotMetException если категория связана с существующими событиями
     */
    void delete(Long id);

    /**
     * Обновляет существующую категорию.
     *
     * @param id идентификатор обновляемой категории
     * @param category DTO с новыми данными категории
     * @return DTO обновленной категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.AlreadyExistsException если категория с новым названием уже существует
     */
    CategoryDto update(Long id, CategoryDto category);

    /**
     * Находит категорию по идентификатору.
     *
     * @param id идентификатор категории
     * @return DTO найденной категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    CategoryDto findById(Long id);

    /**
     * Возвращает список категорий с пагинацией.
     * <p>
     * Результат отсортирован по идентификатору категории.
     * </p>
     *
     * @param params параметры пагинации (from, size)
     * @return список DTO категорий
     */
    List<CategoryDto> findAll(CategoryParam params);
}