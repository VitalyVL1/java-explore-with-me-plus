package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.QEvent;
import ru.practicum.model.event.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Репозиторий для выполнения операций с сущностью {@link Event}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также методы для сложного поиска событий с динамическими фильтрами с использованием
 * QueryDSL {@link QuerydslPredicateExecutor}. Содержит вложенный интерфейс Predicate
 * для построения различных фильтров.
 * </p>
 *
 * @see Event
 * @see JpaRepository
 * @see QuerydslPredicateExecutor
 * @see QEvent
 */
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    /**
     * Внутренний интерфейс, содержащий фабричные методы для создания предикатов QueryDSL.
     * <p>
     * Используется для динамического построения условий поиска событий для различных
     * уровней доступа (административный, публичный).
     * </p>
     */
    interface Predicate {

        /**
         * Создает предикат для административного поиска событий.
         *
         * @param params параметры фильтрации (пользователи, статусы, категории, диапазон дат)
         * @return BooleanBuilder с условиями фильтрации
         */
        static BooleanBuilder adminFilters(AdminEventParam params) {
            BooleanBuilder predicate = new BooleanBuilder();

            addUsersPredicate(predicate, params.users());
            addStatesPredicate(predicate, params.states());
            addCategoriesPredicate(predicate, params.categories());
            addDatePredicate(predicate, params.rangeStart(), params.rangeEnd());

            return predicate;
        }

        /**
         * Создает предикат для публичного поиска событий.
         *
         * @param params параметры фильтрации (текст, категории, платность, диапазон дат, доступность)
         * @return BooleanBuilder с условиями фильтрации
         */
        static BooleanBuilder publicFilters(EventPublicParam params) {
            return publicFilters(params, null);
        }

        /**
         * Создает предикат для публичного поиска событий с учетом доступных событий.
         *
         * @param params параметры фильтрации
         * @param availableIds список ID событий с доступными местами
         * @return BooleanBuilder с условиями фильтрации
         */
        static BooleanBuilder publicFilters(EventPublicParam params, List<Long> availableIds) {
            BooleanBuilder predicate = new BooleanBuilder();

            addStatePredicate(predicate, State.PUBLISHED);
            addTextPredicate(predicate, params.text());
            addPaidPredicate(predicate, params.paid());
            addCategoriesPredicate(predicate, params.categories());
            addDatePredicate(predicate, params.rangeStart(), params.rangeEnd());
            addOnlyAvailablePredicate(predicate, params.onlyAvailable(), availableIds);

            return predicate;
        }

        /**
         * Добавляет фильтр по пользователям (инициаторам событий).
         *
         * @param predicate билдер предиката
         * @param users множество ID пользователей
         */
        private static void addUsersPredicate(BooleanBuilder predicate, Set<Long> users) {
            if (users != null && !users.isEmpty()) {
                predicate.and(QEvent.event.initiator.id.in(users));
            }
        }

        /**
         * Добавляет фильтр по множеству состояний событий.
         *
         * @param predicate билдер предиката
         * @param states множество статусов событий
         */
        private static void addStatesPredicate(BooleanBuilder predicate, Set<State> states) {
            if (states != null && !states.isEmpty()) {
                predicate.and(QEvent.event.state.in(states));
            }
        }

        /**
         * Добавляет фильтр по одиночному состоянию события.
         *
         * @param predicate билдер предиката
         * @param state статус события
         */
        private static void addStatePredicate(BooleanBuilder predicate, State state) {
            if (state != null) {
                predicate.and(QEvent.event.state.eq(state));
            }
        }

        /**
         * Добавляет текстовый поиск в аннотации и описании события.
         *
         * @param predicate билдер предиката
         * @param text текст для поиска
         */
        private static void addTextPredicate(BooleanBuilder predicate, String text) {
            if (text != null && !text.isBlank()) {
                String pattern = "%" + text.toLowerCase() + "%";
                predicate.and(QEvent.event.annotation.lower().like(pattern)
                        .or(QEvent.event.description.lower().like(pattern)));
            }
        }

        /**
         * Добавляет фильтр по категориям.
         *
         * @param predicate билдер предиката
         * @param categories множество ID категорий
         */
        private static void addCategoriesPredicate(BooleanBuilder predicate, Set<Long> categories) {
            if (categories != null && !categories.isEmpty()) {
                predicate.and(QEvent.event.category.id.in(categories));
            }
        }

        /**
         * Добавляет фильтр по платности события.
         *
         * @param predicate билдер предиката
         * @param paid флаг платности
         */
        private static void addPaidPredicate(BooleanBuilder predicate, Boolean paid) {
            if (paid != null) {
                predicate.and(QEvent.event.paid.eq(paid));
            }
        }

        /**
         * Добавляет фильтр по диапазону дат события.
         * <p>
         * Если обе даты указаны - ищет события между ними.
         * Если указано только начало - ищет события после этой даты.
         * Если указан только конец - ищет события до этой даты.
         * Если даты не указаны - ищет будущие события (после текущего момента).
         * </p>
         *
         * @param predicate билдер предиката
         * @param rangeStart начало диапазона
         * @param rangeEnd конец диапазона
         */
        private static void addDatePredicate(BooleanBuilder predicate,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd) {
            if (rangeStart != null && rangeEnd != null) {
                predicate.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
            } else if (rangeStart != null) {
                predicate.and(QEvent.event.eventDate.goe(rangeStart));
            } else if (rangeEnd != null) {
                predicate.and(QEvent.event.eventDate.loe(rangeEnd));
            } else {
                predicate.and(QEvent.event.eventDate.after(LocalDateTime.now()));
            }
        }

        /**
         * Добавляет фильтр по доступности события (наличие свободных мест).
         *
         * @param predicate билдер предиката
         * @param onlyAvailable флаг "только доступные"
         * @param availableIds список ID доступных событий
         */
        private static void addOnlyAvailablePredicate(BooleanBuilder predicate,
                                                      Boolean onlyAvailable,
                                                      List<Long> availableIds) {
            if (onlyAvailable != null && onlyAvailable && availableIds != null && !availableIds.isEmpty()) {
                predicate.and(QEvent.event.id.in(availableIds));
            }
        }
    }

    /**
     * Находит событие по его ID и статусу.
     *
     * @param eventId идентификатор события
     * @param state статус события
     * @return Optional с событием, если оно найдено и имеет указанный статус
     */
    Optional<Event> findByIdAndState(Long eventId, State state);

    /**
     * Находит событие по его ID и ID инициатора.
     *
     * @param eventId идентификатор события
     * @param userId идентификатор пользователя-инициатора
     * @return Optional с событием, если оно найдено и принадлежит указанному пользователю
     */
    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    /**
     * Находит все события указанного инициатора с пагинацией.
     *
     * @param userId идентификатор пользователя-инициатора
     * @param pageable параметры пагинации
     * @return список событий пользователя
     */
    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    /**
     * Находит ID всех событий, у которых есть свободные места для участия.
     * <p>
     * Событие считается доступным, если:
     * <ul>
     *   <li>лимит участников равен 0 (без лимита)</li>
     *   <li>количество подтвержденных заявок меньше лимита участников</li>
     * </ul>
     *
     * @return список ID событий со свободными местами
     */
    @Query("""
            SELECT e.id
            FROM Event e
            LEFT JOIN Request r ON r.event.id = e.id AND r.status = 'CONFIRMED'
            GROUP BY e.id, e.participantLimit
            HAVING e.participantLimit = 0 OR COUNT(r) < e.participantLimit
            """)
    List<Long> findEventIdsWithAvailableSlots();
}