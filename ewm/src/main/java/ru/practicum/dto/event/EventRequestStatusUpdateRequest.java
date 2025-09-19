package ru.practicum.dto.event;

import java.util.Set;

public record EventRequestStatusUpdateRequest(
        Set<Long> requestIds,
        RequestStatus status
) {
}
