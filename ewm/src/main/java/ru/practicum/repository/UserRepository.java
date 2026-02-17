package ru.practicum.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.user.QUser;
import ru.practicum.model.user.User;

import java.util.List;

/**
 * Репозиторий для выполнения операций с сущностью {@link User}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также возможность динамического построения запросов с использованием
 * QueryDSL {@link QuerydslPredicateExecutor}. Содержит вложенный интерфейс Predicate
 * для создания условий фильтрации.
 * </p>
 *
 * @see User
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 * @see QUser
 */
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    /**
     * Внутренний интерфейс, содержащий фабричные методы для создания предикатов QueryDSL.
     * <p>
     * Используется для динамического построения условий поиска пользователей.
     * </p>
     */
    interface Predicate {

        /**
         * Создает предикат для фильтрации пользователей по списку идентификаторов.
         * <p>
         * Возвращает условие, которое выбирает пользователей, чьи ID входят в переданный список.
         * </p>
         *
         * @param ids список идентификаторов пользователей
         * @return BooleanExpression для фильтрации по ID
         */
        static BooleanExpression byIds(List<Long> ids) {
            QUser user = QUser.user;
            return user.id.in(ids);
        }
    }
}