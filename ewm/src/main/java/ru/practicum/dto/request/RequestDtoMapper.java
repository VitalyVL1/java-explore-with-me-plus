package ru.practicum.dto.request;

import ru.practicum.model.request.Request;

public class RequestDtoMapper {
    public static ParticipationRequestDto mapRequestToDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .created(request.getCreatedOn())
                .eventId(request.getEvent().getId())
                .requesterId(request.getRequester().getId())
                .build();
    }
}
