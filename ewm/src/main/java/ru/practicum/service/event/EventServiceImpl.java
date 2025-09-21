package ru.practicum.service.event;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestDtoMapper;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.dto.event.*;
import ru.practicum.exception.EventConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventSort;
import ru.practicum.model.event.State;
import ru.practicum.model.event.mapper.EventFullDtoMapper;
import ru.practicum.model.event.mapper.EventMapper;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.OffsetBasedPageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private static final long MIN_HOURS_BEFORE_PUBLICATION_FOR_ADMIN = 1;
    private final EventMapper eventMapper;

    @Override
    public List<EventFullDto> findAllAdmin(AdminEventParam params) {
        int from = params.from();
        int size = params.size();

        Page<Event> eventsPage = eventRepository.findAll(EventRepository.Predicate.adminFilters(params), getPageRequest(from, size, null));
        List<Event> events = eventsPage.getContent();

        if (eventsPage.isEmpty()) {
            return Collections.emptyList();
        }

        if (from > 0 && events.size() > from) {
            events = events.subList(from, events.size());
        }

        if (events.size() > size) {
            events = events.subList(0, size);
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> views = getViewsForEvents(eventIds);
        Map<Long, Long> confirmedRequests = getConfirmedRequestsForEvents(eventIds);

        return events.stream()
                .map(event -> EventFullDtoMapper.mapToEventFullDto(event, confirmedRequests.get(event.getId()), views.get(event.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest event) {
        Event eventModel = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдена"));

        if (event.annotation() != null) {
            eventModel.setAnnotation(event.annotation());
        }

        if (event.category() != null) {
            Category category = categoryRepository.findById(event.category())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + event.category() + " not found for eventModel update."));
            eventModel.setCategory(category);
        }

        if (event.description() != null) {
            eventModel.setDescription(event.description());
        }

        if (event.eventDate() != null) {
            eventModel.setEventDate(event.eventDate());
        }

        if (event.location() != null) {
            eventModel.setLocation(event.location());
        }

        if (event.paid() != null) {
            eventModel.setPaid(event.paid());
        }

        if (event.participantLimit() != null) {
            eventModel.setParticipantLimit(event.participantLimit());
        }

        if (event.requestModeration() != null) {
            eventModel.setRequestModeration(event.requestModeration());
        }

        if (event.title() != null) {
            eventModel.setTitle(event.title());
        }

        if (event.stateAction() != null) {
            switch (event.stateAction()) {
                case PUBLISH_EVENT:
                    if (eventModel.getState() != State.PENDING) {
                        throw new ConditionsNotMetException("Событие можно публиковать, только если оно в состоянии ожидания публикации. Настоящее состояние: " + eventModel.getState());
                    }
                    if (eventModel.getEventDate().isBefore(LocalDateTime.now().plusHours(MIN_HOURS_BEFORE_PUBLICATION_FOR_ADMIN))) {
                        throw new ConditionsNotMetException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                    }
                    eventModel.setState(State.PUBLISHED);
                    eventModel.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (eventModel.getState() == State.PUBLISHED) {
                        throw new ConditionsNotMetException("Событие можно отклонить, только если оно еще не опубликовано");
                    }
                    eventModel.setState(State.CANCELED);
                    break;
                default:
                    break;
            }
        }

        Long views = getViews(eventModel.getId());
        System.out.println(views);
        Long confirmedRequests = getConfirmedRequests(eventModel.getId());
        System.out.println(confirmedRequests);
        return EventFullDtoMapper.mapToEventFullDto(eventRepository.save(eventModel), confirmedRequests, views);
    }

    private Pageable getPageRequest(int from, int size, Sort sort) {
        int pageSize = from + size;

        if (sort == null) {
            return PageRequest.of(0, pageSize);
        }
        return PageRequest.of(0, pageSize, sort);
    }

    //TODO
    @Override
    public List<EventShortDto> findPublicEvents(EventPublicParam params) {
        Specification<Event> spec = buildSpecification(params);
        List<Event> events = eventRepository.findAll(spec);

        setViewsAndConfirmedRequests(events);

        if (params.onlyAvailable() != null && params.onlyAvailable()) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                                     event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        Comparator<EventShortDto> comparator = createEventShortDtoComparator(params.sort());

        return events.stream()
                .map(eventMapper::toShortDto)
                .sorted(comparator)
                .skip(params.from())
                .limit(params.size())
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

    @Override
    public List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        Pageable pageable = new OffsetBasedPageable(params.from(), params.size(), Sort.by("id").descending());
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);

        setViewsAndConfirmedRequests(events);

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
        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(
                () -> new NotFoundException(String.format("Event with id %d by user %d not found", eventId, userId))
        );
        setViewsAndConfirmedRequests(event);
        return eventMapper.toFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(UpdateEventUserRequestParam requestParam) {
        LocalDateTime now = LocalDateTime.now();
        Event event = eventRepository.findByIdAndInitiator_Id(requestParam.eventId(), requestParam.userId())
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
        Event event = eventRepository.findByIdAndInitiator_Id(requestParam.eventId(), requestParam.userId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format(
                                        "Event with id %d by user %d not found",
                                        requestParam.eventId(),
                                        requestParam.userId())));

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

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyMap();
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
            return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
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

    private Map<Long, Long> getConfirmedRequestsForEvents(List<Long> eventIds) {
        try {
            return requestRepository.countConfirmedRequestsByEventIds(eventIds);
        } catch (Exception e) {
            return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
        }
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

    private void setViewsAndConfirmedRequests(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> viewsMap = getViewsForEvents(eventIds);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsForEvents(eventIds);

        events.forEach(event -> {
            event.setViews(viewsMap.getOrDefault(event.getId(), 0L));
            event.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L));
        });
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

    private Comparator<EventShortDto> createEventShortDtoComparator(EventSort sort) {
        Comparator<EventShortDto> comparator;

        if (sort == null) {
            comparator = (a, b) -> 0;
        } else {
            comparator = switch (sort) {
                case VIEWS -> Comparator.comparing(EventShortDto::views).reversed();
                case EVENT_DATE -> Comparator.comparing(EventShortDto::eventDate);
            };
        }
        return comparator;
    }
}
