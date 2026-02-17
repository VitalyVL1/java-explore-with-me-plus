package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.category.Category;

import java.util.List;

/**
 * Репозиторий для выполнения операций с сущностью {@link Category}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также дополнительные методы для проверки уникальности категории по имени
 * и пагинированного получения списка категорий.
 * </p>
 *
 * @see Category
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 */
public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {

    /**
     * Проверяет существование категории с указанным именем (регистронезависимо, с игнорированием пробелов).
     * <p>
     * Используется при создании и обновлении категорий для обеспечения уникальности имени.
     * При сравнении приводит имя к нижнему регистру и удаляет начальные/конечные пробелы.
     * </p>
     *
     * @param name имя категории для проверки
     * @return true если категория с таким именем существует, иначе false
     */
    @Query(
            "select case when count(c) > 0 then true else false end " +
            "from Category c where lower(trim(c.name)) = lower(trim(:name))"
    )
    boolean existsByNameIgnoreCaseAndTrim(@Param("name") String name);

    /**
     * Возвращает список категорий с пагинацией, используя нативный SQL запрос.
     * <p>
     * Категории сортируются по идентификатору (id) в порядке возрастания.
     * Параметры пагинации: OFFSET - количество пропускаемых записей, LIMIT - размер страницы.
     * </p>
     *
     * @param from количество записей для пропуска (OFFSET)
     * @param size количество записей для возврата (LIMIT)
     * @return список категорий для указанной страницы
     */
    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT :size OFFSET :from", nativeQuery = true)
    List<Category> findAllWithOffset(@Param("from") int from,
                                     @Param("size") int size);
}