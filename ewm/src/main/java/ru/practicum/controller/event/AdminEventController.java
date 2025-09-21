package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.model.event.State;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findAll(
            @RequestParam(name = "users", required = false)
            List<Long> users,

            @RequestParam(name = "states", required = false)
            List<State> states,

            @RequestParam(name = "categories", required = false)
            List<Long> categories,

            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
            LocalDateTime rangeStart,

            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
            LocalDateTime rangeEnd,

            @RequestParam(name = "from", defaultValue = "0")
            @PositiveOrZero
            int from,

            @RequestParam(name = "size", defaultValue = "10")
            @Positive
            int size
    ) {
        if (rangeStart != null && rangeEnd != null && !rangeEnd.isAfter(rangeStart)) {
            throw new ValidationException("Дата конца диапазона должна быть позже даты начала");
        }

        log.info("Admin: Method launched (findAll(List<Long> users = {}, List<State> states = {}, List<Long> categories = {}," +
                        "LocalDateTime rangeStart = {}, LocalDateTime rangeEnd = {}, int from = {}, int size = {}))",
                users, states, categories, rangeStart, rangeEnd, from, size);
        AdminEventParam eventParam = new AdminEventParam(users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.findAllAdmin(eventParam);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest event
    ) {
        log.info("Admin: Method launched (delete(LLong eventId = {}, UpdateEventAdminRequest event = {}))", eventId, event);
        return eventService.updateAdminEvent(eventId, event);
    }
}
