package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndState(Long eventId, State state);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    @Query(value = """
        SELECT e.* FROM events e
        WHERE e.state = 'PUBLISHED'
        AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
             OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
        AND (:categories IS NULL OR e.category_id IN :categories)
        AND (:paid IS NULL OR e.paid = :paid)
        AND (:rangeStart IS NULL OR e.event_date >= :rangeStart)
        AND (:rangeEnd IS NULL OR e.event_date <= :rangeEnd)
        AND (:onlyAvailable = false OR e.participant_limit = 0 OR 
             e.participant_limit > (
                 SELECT COUNT(*) FROM requests r 
                 WHERE r.event_id = e.id AND r.status = 'CONFIRMED'
             ))
        ORDER BY 
            CASE WHEN :sort = 'EVENT_DATE' THEN e.event_date END DESC,
            CASE WHEN :sort = 'VIEWS' THEN e.views END DESC,
            e.id ASC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<Event> findPublicEventsNative(
            @Param("text") String text,
            @Param("categories") Set<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("sort") String sort,
            @Param("limit") int limit,
            @Param("offset") int offset);


}
