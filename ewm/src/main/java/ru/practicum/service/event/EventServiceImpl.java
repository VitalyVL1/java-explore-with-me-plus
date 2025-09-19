package ru.practicum.service.event;

import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params) {
        return List.of();
    }

    @Override
    public EventFullDto findPublicEventById(Long eventId) {
        return null;
    }

    @Override
    public List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params) {
        return List.of();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        return null;
    }

    @Override
    public EventFullDto findUserEventById(Long eventId, Long userId) {
        return null;
    }

    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequestParam requestParam) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long eventId, Long userId) {
        return List.of();
    }

    @Override
    public List<EventRequestStatusUpdateResult> updateRequestStatus(EventRequestStatusUpdateRequestParam requestParam) {
        return List.of();
    }
}
