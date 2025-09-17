package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {
    @Query(
            "select case when count(c) > 0 then true else false end " +
            "from Category c where lower(trim(c.name)) = lower(trim(:name))"
    )
    boolean existsByNameIgnoreCaseAndTrim(@Param("name") String name);
}
