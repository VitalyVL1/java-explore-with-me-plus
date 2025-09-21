package ru.practicum.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.dto.event.AdminEventParam;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.QEvent;
import ru.practicum.model.event.State;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, JpaSpecificationExecutor<Event> {
    interface Predicate {
        static BooleanBuilder adminFilters(AdminEventParam params) {
            List<Long> users = params.users();
            List<State> states = params.states();
            List<Long> categories = params.categories();
            LocalDateTime rangeStart = params.rangeStart();
            LocalDateTime rangeEnd = params.rangeEnd();

            QEvent qEvent = QEvent.event;
            BooleanBuilder predicate = new BooleanBuilder();

            if (users != null && !users.isEmpty()) {
                predicate.and(qEvent.initiator.id.in(users));
            }

            if (states != null && !states.isEmpty()) {
                predicate.and(qEvent.state.in(states));
            }

            if (categories != null && !categories.isEmpty()) {
                predicate.and(qEvent.category.id.in(categories));
            }

            if (rangeStart != null) {
                predicate.and(qEvent.eventDate.goe(rangeStart));
            }

            if (rangeEnd != null) {
                predicate.and(qEvent.eventDate.loe(rangeEnd));
            }

            return predicate;
        }

        static BooleanBuilder publicFilters(EventPublicParam params) {
            String text = params.text();
            Set<Long> categories = params.categories();
            Boolean paid = params.paid();
            LocalDateTime rangeStart = params.rangeStart();
            LocalDateTime rangeEnd = params.rangeEnd();

            QEvent qEvent = QEvent.event;
            BooleanBuilder predicate = new BooleanBuilder();

            predicate.and(qEvent.state.eq(State.PUBLISHED));

            if (text != null && !text.isBlank()) {
                String searchText = text.toLowerCase();
                predicate.and(qEvent.annotation.lower().like("%" + searchText + "%")
                        .or(qEvent.description.lower().like("%" + searchText + "%")));
            }

            if (categories != null && !categories.isEmpty()) {
                predicate.and(qEvent.category.id.in(categories));
            }

            if (paid != null) {
                predicate.and(qEvent.paid.eq(paid));
            }

            if (rangeStart == null && rangeEnd == null) {
                predicate.and(qEvent.eventDate.after(LocalDateTime.now()));
            } else {
                if (rangeStart != null) {
                    predicate.and(qEvent.eventDate.goe(rangeStart));
                }
                if (rangeEnd != null) {
                    predicate.and(qEvent.eventDate.loe(rangeEnd));
                }
            }

            return predicate;
        }
    }
    Optional<Event> findByIdAndState(Long eventId, State state);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);
    
    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    List<Event> findAllByInitiator_Id(Long userId);
}
