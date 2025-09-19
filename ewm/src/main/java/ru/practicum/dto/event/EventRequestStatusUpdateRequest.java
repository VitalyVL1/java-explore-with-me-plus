package ru.practicum.dto.event;

import ru.practicum.model.request.RequestStatus;

import java.util.Set;

public record EventRequestStatusUpdateRequest(
        Set<Long> requestIds,
        RequestStatus status
) {
}
