package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.QCompilation;

import com.querydsl.core.types.Predicate;

/**
 * Репозиторий для выполнения операций с сущностью {@link Compilation}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также возможность динамического построения запросов с использованием
 * QueryDSL {@link QuerydslPredicateExecutor}.
 * </p>
 *
 * @see Compilation
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 * @see QCompilation
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long>, QuerydslPredicateExecutor<Compilation> {

    /**
     * Внутренний интерфейс, содержащий фабричные методы для создания предикатов QueryDSL.
     * <p>
     * Используется для динамического построения условий поиска подборок.
     * </p>
     */
    interface Predicates {

        /**
         * Создает предикат для фильтрации подборок по признаку закрепления.
         * <p>
         * Если параметр pinned не указан (null), возвращается пустой предикат,
         * который не добавляет условий фильтрации.
         * </p>
         *
         * @param pinned признак закрепления подборки (true - закрепленные,
         *               false - незакрепленные, null - все подборки)
         * @return предикат для фильтрации подборок по pinned или пустой предикат
         */
        static Predicate buildPredicates(Boolean pinned) {
            BooleanBuilder bb = new BooleanBuilder();

            if (pinned != null) {
                bb.and(QCompilation.compilation.pinned.eq(pinned));
            }

            return bb;
        }
    }
}