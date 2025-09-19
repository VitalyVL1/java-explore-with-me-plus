package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@Valid @ModelAttribute EventPublicParam params) {
        log.info("Public: Method launched (findAllPublic({}))", params);
        return eventService.findAllPublic(params);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(
            @Positive(message = "eventId должен быть больше 0")
            @PathVariable
            Long eventId
    ) {
        log.info("Public: Method launched (findByIdPublic({}))", eventId);
        return eventService.findByIdPublic(eventId);
    }
}
