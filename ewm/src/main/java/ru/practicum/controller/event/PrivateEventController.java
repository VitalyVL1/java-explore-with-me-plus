package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
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
    public List<EventShortDto> getEventsByUserId(
            @PathVariable
            @Positive(message = "Id должен быть больше 0")
            Long userId,

            @Valid
            @ModelAttribute
            EventPrivateParam params
    ) {
        log.info("Private: Method launched (findAllByUserId({}))", params);
        return eventService.findAllByUserId(userId, params);
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
        log.info("Private: Method launched (save({}, {}))", userId, dto);
        return eventService.save(userId, dto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdAndUserId(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId
    ) {
        log.info("Private: Method launched (updateEvent({}))", findByIdAndUserId);
        return eventService.findByIdAndUserId(updateEventRequestParam);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId,

            @Valid
            @RequestBody
            UpdateEventRequest updateRequest
    ) {
        UpdateEventRequestParam updateEventRequestParam =
                new UpdateEventRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateEvent({}))", updateEventRequestParam);
        return eventService.updateEvent(updateEventRequestParam);
    }


    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto getRequestsByEventIdAndUserId(
            @PathVariable
            @Positive(message = "userId должен быть больше 0")
            Long userId,

            @PathVariable
            @Positive(message = "eventId должен быть больше 0")
            Long eventId
    ) {
        log.info("Private: Method launched (updateEvent({}, {}))", eventId, userId);
        return eventService.findAllRequestsByEventIdAndUserId(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequest(
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
        log.info("Private: Method launched (updateEvent({}, {}))", eventId, userId);
        return eventService.findAllRequestsByEventIdAndUserId(eventId, userId);
    }

}
