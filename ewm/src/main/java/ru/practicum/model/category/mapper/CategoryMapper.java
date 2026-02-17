package ru.practicum.model.category.mapper;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.category.Category;

/**
 * Утилитарный класс-маппер для преобразования между сущностью категории и DTO.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link Category}
 * в объект {@link CategoryDto} и обратно. Используется для отделения доменной модели
 * от объектов передачи данных.
 * </p>
 *
 * @see Category
 * @see CategoryDto
 */
public class CategoryMapper {

    /**
     * Преобразует DTO категории в сущность категории.
     * <p>
     * Создает объект {@link Category} на основе данных из DTO.
     * Идентификатор не копируется, так как при создании новой категории
     * он генерируется базой данных.
     * </p>
     *
     * @param dto DTO категории с данными (название)
     * @return сущность категории, готовую для сохранения в БД
     */
    public static Category mapToCategory(CategoryDto dto) {
        return Category.builder()
                .name(dto.name())
                .build();
    }

    /**
     * Преобразует сущность категории в DTO.
     * <p>
     * Создает объект {@link CategoryDto} на основе данных из сущности категории,
     * включая сгенерированный базой данных идентификатор.
     * </p>
     *
     * @param category сущность категории
     * @return DTO категории, готовое для отправки клиенту
     */
    public static CategoryDto mapToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}