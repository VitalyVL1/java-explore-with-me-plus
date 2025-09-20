package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    Optional<Event> findByIdAndState(Long eventId, State state);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);
}
