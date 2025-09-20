package ru.practicum.service.event;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestDtoMapper;
import ru.practicum.exception.EventConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.State;
import ru.practicum.model.event.mapper.EventMapper;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;

    //TODO
    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params) {
        Specification<Event> spec = buildSpecification(params);
        Sort sort = createSort(params.sort());
        Pageable pageable = createPageable(params.from(), params.size(), sort);

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        events.forEach(this::setViewsAndConfirmedRequests);

        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findPublicEventById(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Event with id %d not found", eventId))
                );
        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(event);
    }

    //TODO
    @Override
    public List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }

        Pageable pageable = PageRequest.of(params.from(), params.size());
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);

        events.forEach(this::setViewsAndConfirmedRequests);

        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d not found", userId)));

        Category category = categoryRepository.findById(dto.category()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id %d not found", dto.category())));

        Event event = eventRepository.save(eventMapper.toEntity(dto, user, category));

        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto findUserEventById(Long eventId, Long userId) {
        Event event = eventRepository.findByIdAndUserId(eventId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id %d by user %d not found", eventId, userId))
        );
        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequestParam requestParam) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findByIdAndUserId(requestParam.eventId(), requestParam.userId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Event with id %d by user %d not found",
                                        requestParam.eventId(),
                                        requestParam.userId())));

        UpdateEventUserRequest updateRequest = requestParam.request();

        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventConflictException("Ожидается статус PENDING или CANCELED, получен - " + event.getState());
        }

        if (now.plusHours(2).isAfter(event.getEventDate())) {
            throw new EventConflictException("Изменить можно события запланированные " +
                                             "на время не ранее чем через 2 часа от текущего, разница времени - " +
                                             Duration.between(now, event.getEventDate()).toHours());
        }

        updateEvent(event, updateRequest);
        eventRepository.save(event);
        setViewsAndConfirmedRequests(event);

        return eventMapper.toFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long eventId, Long userId) {
        List<Request> requests =
                requestRepository.findByEvent_IdAndEvent_Initiator_Id(eventId, userId);
        return RequestDtoMapper.mapRequestToDto(requests);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequestParam requestParam) {
        EventRequestStatusUpdateRequest updateRequest = requestParam.updateRequest();
        Event event = eventRepository.findByIdAndUserId(requestParam.eventId(), requestParam.userId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Event with id %d by user %d not found",
                                        requestParam.eventId(),
                                        requestParam.userId())));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventConflictException("Нельзя изменять статусы заявок для неопубликованного события");
        }

        setConfirmedRequests(event);

        if (event.getParticipantLimit() != 0 &&
            event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new EventConflictException("Достигнут лимит по заявкам на событие - " + event.getId());
        }

        List<Request> requestsToUpdate = requestRepository.findAllById(updateRequest.requestIds());

        requestsToUpdate.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new EventConflictException(
                        "Статус можно изменить только у заявок в состоянии ожидания. " +
                        "Текущий статус заявки " + request.getId() + ": " + request.getStatus());
            }
        });

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            requestsToUpdate.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            requestRepository.saveAll(requestsToUpdate);

            return new EventRequestStatusUpdateResult(RequestDtoMapper.mapRequestToDto(requestsToUpdate), List.of());
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        long availableSlots = event.getParticipantLimit() - event.getConfirmedRequests();

        for (Request request : requestsToUpdate) {
            if (availableSlots > 0 && updateRequest.status().equals(RequestStatus.CONFIRMED)) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                availableSlots--;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        requestRepository.saveAll(requestsToUpdate);

        return new EventRequestStatusUpdateResult(
                RequestDtoMapper.mapRequestToDto(confirmedRequests),
                RequestDtoMapper.mapRequestToDto(rejectedRequests)
        );
    }

    private Specification<Event> buildSpecification(EventPublicParam params) {
        return Stream.<Supplier<Specification<Event>>>of(
                        () -> (root, query, cb) ->
                                cb.equal(root.get("state"), State.PUBLISHED),
                        () -> createOptionalSpec(params.text(), this::createTextSpec),
                        () -> createOptionalSpec(params.categories(), this::createCategoriesSpec),
                        () -> createOptionalSpec(params.paid(), this::createPaidSpec),
                        () -> createOptionalSpec(params.onlyAvailable(), this::createAvailabilitySpec),
                        () -> createDateSpec(params.rangeStart(), params.rangeEnd())
                )
                .map(Supplier::get)
                .reduce(Specification::and)
                .orElse(Specification.unrestricted());
    }

    private <T> Specification<Event> createOptionalSpec(@Nullable T value,
                                                        Function<T, Specification<Event>> specCreator) {
        if (value != null && !isEmpty(value)) {
            return specCreator.apply(value);
        }
        return Specification.unrestricted();
    }

    private boolean isEmpty(Object value) {
        if (value instanceof Collection) return ((Collection<?>) value).isEmpty();
        if (value instanceof String) return ((String) value).isBlank();
        return false;
    }

    private Specification<Event> createTextSpec(String text) {
        return (root, query, cb) -> {
            String pattern = "%" + text.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("annotation")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    private Specification<Event> createCategoriesSpec(Set<Long> categories) {
        return (root, query, cb) ->
                root.get("category").get("id").in(categories);
    }

    private Specification<Event> createPaidSpec(Boolean paid) {
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }

    private Specification<Event> createAvailabilitySpec(Boolean onlyAvailable) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("participantLimit"), 0),
                cb.greaterThan(root.get("participantLimit"), root.get("confirmedRequests"))
        );
    }

    private Specification<Event> createDateSpec(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, cb) -> {
            if (rangeStart != null && rangeEnd != null) {
                return cb.between(root.get("eventDate"), rangeStart, rangeEnd);
            } else if (rangeStart != null) {
                return cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
            } else if (rangeEnd != null) {
                return cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
            } else {
                return cb.greaterThan(root.get("eventDate"), LocalDateTime.now());
            }
        };
    }

    private Pageable createPageable(Integer from, Integer size, Sort sort) {
        return PageRequest.of(from, size, sort);
    }

    private Sort createSort(EventSort sort) {
        return switch (sort) {
            case EVENT_DATE -> Sort.by(Sort.Order.asc("eventDate"));
            case VIEWS -> Sort.by(Sort.Order.desc("views"), Sort.Order.asc("eventDate"));
        };
    }

    public Map<Long, Long> getViewsStats(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        try {
            List<ResponseStatsDto> stats = statsClient.get(createRequestStatsDto(uris, true));

            return stats.stream()
                    .collect(Collectors.toMap(
                            stat -> extractEventIdFromUri(stat.uri()),
                            ResponseStatsDto::hits,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            return Map.of();
        }
    }

    private Long extractEventIdFromUri(String uri) {
        try {
            return Long.parseLong(uri.replace("/events/", ""));
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private Long getViews(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        Long views = 0L;
        try {
            views = statsClient.get(createRequestStatsDto(uris, true))
                    .getFirst()
                    .hits();
        } catch (Exception e) {
            return views;
        }
        return views;
    }

    private Long getConfirmedRequests(Long eventId) {
        Long confirmedRequests = 0L;
        try {
            confirmedRequests = requestRepository
                    .countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        } catch (Exception e) {
            return confirmedRequests;
        }
        return confirmedRequests;
    }

    private RequestStatsDto createRequestStatsDto(List<String> uris, boolean unique) {
        return new RequestStatsDto(
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now(),
                uris,
                unique
        );
    }

    private void setViewsAndConfirmedRequests(Event event) {
        setViews(event);
        setConfirmedRequests(event);
    }

    private void setViews(Event event) {
        event.setViews(getViews(event.getId()));
    }

    private void setConfirmedRequests(Event event) {
        event.setConfirmedRequests(getConfirmedRequests(event.getId()));
    }

    private void updateEvent(Event event, UpdateEventUserRequest updateRequest) {
        Optional.ofNullable(updateRequest.annotation())
                .filter(ann -> !ann.isBlank()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateRequest.description())
                .filter(desc -> !desc.isBlank()).ifPresent(event::setDescription);
        Optional.ofNullable(updateRequest.eventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateRequest.location()).ifPresent(event::setLocation);
        Optional.ofNullable(updateRequest.paid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateRequest.participantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateRequest.requestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateRequest.title()).filter(title -> !title.isBlank()).ifPresent(event::setTitle);

        Optional.ofNullable(updateRequest.category()).ifPresent(
                categoryId -> event.setCategory(categoryRepository.findById(categoryId)
                        .orElseThrow(
                                () -> new NotFoundException("Category id " + categoryId + " not found")
                        ))
        );

        if (updateRequest.stateAction() != null) {
            switch (updateRequest.stateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
            }
        }
    }
}
