package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    interface Predicate {
        /**
         * Фильтры для административного поиска событий
         */
        static BooleanBuilder adminFilters(AdminEventParam params) {
            QEvent qEvent = QEvent.event;
            BooleanBuilder predicate = new BooleanBuilder();

            addUsersPredicate(qEvent, predicate, params.users());
            addStatesPredicate(qEvent, predicate, params.states());
            addCategoriesPredicate(qEvent, predicate, params.categories());
            addDatePredicate(qEvent, predicate, params.rangeStart(), params.rangeEnd());

            return predicate;
        }

        /**
         * Фильтры для общедоступного поиска событий
         */
        static BooleanBuilder publicFilters(EventPublicParam params) {
            QEvent qEvent = QEvent.event;
            BooleanBuilder predicate = new BooleanBuilder();

            addStatePredicate(qEvent, predicate, State.PUBLISHED);
            addTextPredicate(qEvent, predicate, params.text());
            addPaidPredicate(qEvent, predicate, params.paid());
            addCategoriesPredicate(qEvent, predicate, params.categories());
            addDatePredicate(qEvent, predicate, params.rangeStart(), params.rangeEnd());

            return predicate;
        }

        /**
         * Добавляет фильтр по пользователям (инициаторам событий)
         */
        private static void addUsersPredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                Set<Long> users
        ) {
            if (users != null && !users.isEmpty()) {
                predicate.and(qEvent.initiator.id.in(users));
            }
        }

        /**
         * Добавляет фильтр по множеству состояний событий
         */
        private static void addStatesPredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                Set<State> states
        ) {
            if (states != null && !states.isEmpty()) {
                predicate.and(qEvent.state.in(states));
            }
        }

        /**
         * Добавляет фильтр по одиночному состоянию события
         */
        private static void addStatePredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                State state
        ) {
            if (state != null) {
                predicate.and(qEvent.state.eq(state));
            }
        }

        /**
         * Добавляет текстовый поиск в annotation и description
         */
        private static void addTextPredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                String text
        ) {
            if (text != null && !text.isBlank()) {
                String pattern = "%" + text.toLowerCase() + "%";
                predicate.and(qEvent.annotation.lower().like(pattern)
                        .or(qEvent.description.lower().like(pattern)));
            }
        }

        /**
         * Добавляет фильтр по категориям
         */
        private static void addCategoriesPredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                Set<Long> categories
        ) {
            if (categories != null && !categories.isEmpty()) {
                predicate.and(qEvent.category.id.in(categories));
            }
        }

        /**
         * Добавляет фильтр по платности события
         */
        private static void addPaidPredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                Boolean paid
        ) {
            if (paid != null) {
                predicate.and(qEvent.paid.eq(paid));
            }
        }

        /**
         * Добавляет фильтр по диапазону дат события
         */
        private static void addDatePredicate(
                QEvent qEvent,
                BooleanBuilder predicate,
                LocalDateTime rangeStart,
                LocalDateTime rangeEnd
        ) {
            if (rangeStart != null && rangeEnd != null) {
                predicate.and(qEvent.eventDate.between(rangeStart, rangeEnd));
            } else if (rangeStart != null) {
                predicate.and(qEvent.eventDate.goe(rangeStart));
            } else if (rangeEnd != null) {
                predicate.and(qEvent.eventDate.loe(rangeEnd));
            } else {
                predicate.and(qEvent.eventDate.after(LocalDateTime.now()));
            }
        }
    }

    Optional<Event> findByIdAndState(Long eventId, State state);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);
}
