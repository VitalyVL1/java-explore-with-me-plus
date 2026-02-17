package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryParam;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

/**
 * Реализация сервиса для управления категориями событий.
 * <p>
 * Обеспечает бизнес-логику для операций с категориями: создание, обновление,
 * удаление, поиск по идентификатору и получение списка с пагинацией.
 * Все операции с базой данных транзакционны.
 * </p>
 *
 * @see CategoryService
 * @see CategoryRepository
 * @see CategoryMapper
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Сохраняет новую категорию.
     * <p>
     * Перед сохранением проверяет уникальность названия категории.
     * </p>
     *
     * @param category DTO с данными категории
     * @return DTO созданной категории
     * @throws AlreadyExistsException если категория с таким названием уже существует
     */
    @Override
    @Transactional
    public CategoryDto save(CategoryDto category) {
        Category saveCat;
        if (categoryRepository.existsByNameIgnoreCaseAndTrim(category.name())) {
            throw new AlreadyExistsException("Категория с названием " + category.name() + " уже существует");
        }
        saveCat = categoryRepository.save(CategoryMapper.mapToCategory(category));

        return CategoryMapper.mapToCategoryDto(saveCat);
    }

    /**
     * Удаляет категорию по идентификатору.
     * <p>
     * Перед удалением проверяет существование категории.
     * В случае наличия связанных событий будет выброшено исключение на уровне БД.
     * </p>
     *
     * @param id идентификатор удаляемой категории
     * @throws NotFoundException если категория с указанным ID не найдена
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Категория с id " + id + " не найдена");
        }

        categoryRepository.deleteById(id);
    }

    /**
     * Обновляет существующую категорию.
     * <p>
     * Проверяет существование категории и уникальность нового названия.
     * Если новое название совпадает со старым, проверка уникальности пропускается.
     * </p>
     *
     * @param id идентификатор обновляемой категории
     * @param category DTO с новыми данными категории
     * @return DTO обновленной категории
     * @throws NotFoundException если категория с указанным ID не найдена
     * @throws AlreadyExistsException если категория с новым названием уже существует
     */
    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto category) {
        String catDtoName = category.name();
        Category getCat = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));

        if (
                categoryRepository.existsByNameIgnoreCaseAndTrim(catDtoName) &&
                !getCat.getName().equalsIgnoreCase(catDtoName)
        ) {
            throw new AlreadyExistsException("Категория с названием " + catDtoName + " уже существует");
        }

        getCat.setName(catDtoName);

        return CategoryMapper.mapToCategoryDto(categoryRepository.save(getCat));
    }

    /**
     * Находит категорию по идентификатору.
     *
     * @param id идентификатор категории
     * @return DTO найденной категории
     * @throws NotFoundException если категория с указанным ID не найдена
     */
    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }

    /**
     * Возвращает список категорий с пагинацией.
     * <p>
     * Использует кастомный метод репозитория для получения категорий
     * с учетом смещения и ограничения количества.
     * </p>
     *
     * @param params параметры пагинации (from, size)
     * @return список DTO категорий, отсортированных по идентификатору
     */
    @Override
    public List<CategoryDto> findAll(CategoryParam params) {
        List<Category> categories = categoryRepository.findAllWithOffset(params.from(), params.size());

        return categories.stream()
                .map(CategoryMapper::mapToCategoryDto)
                .toList();
    }
}