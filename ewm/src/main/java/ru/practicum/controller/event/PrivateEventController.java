package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findUserEvents(
            @PathVariable
            @Positive(message = "Id должен быть больше 0")
            Long userId,

            @Valid
            @ModelAttribute
            EventPrivateParam params
    ) {
        log.info("Private: Method launched (findUserEvents({}))", params);
        return eventService.findUserEvents(userId, params);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable
            @Positive(message = "Id должен быть больше 0")
            Long userId,

            @Valid
            @RequestBody
            NewEventDto dto
    ) {
        log.info("Private: Method launched (createEvent({}, {}))", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findUserEventById(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId
    ) {
        log.info("Private: Method launched (findUserEventById({}, {}))", eventId, userId);
        return eventService.findUserEventById(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateUserEvent(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId,

            @Valid
            @RequestBody
            UpdateEventUserRequest updateRequest
    ) {
        UpdateEventUserRequestParam updateEventUserRequestParam =
                new UpdateEventUserRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateUserEvent({}))", updateEventUserRequestParam);
        return eventService.updateUserEvent(updateEventUserRequestParam);
    }


    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findEventRequests(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId
    ) {
        log.info("Private: Method launched (findEventRequests({}, {}))", eventId, userId);
        return eventService.findEventRequests(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId,

            @Valid
            @RequestBody
            EventRequestStatusUpdateRequest updateRequest
    ) {
        EventRequestStatusUpdateRequestParam updateEventRequestParam =
                new EventRequestStatusUpdateRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateRequestStatus({}))", updateEventRequestParam);
        return eventService.updateRequestStatus(updateEventRequestParam);
    }
}
