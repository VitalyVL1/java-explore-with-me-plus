package ru.practicum.model.event.mapper;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.model.category.mapper.CategoryMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.mapper.UserMapper;

public class EventFullDtoMapper {
    public static EventFullDto mapToEventFullDto(
            Event event,
            Long confirmedRequests,
            Long views
    ) {
        if (confirmedRequests == null) confirmedRequests = 0L;
        return EventFullDto.builder()
                .id(event.getId())
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .state(event.getState())
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .paid(event.getPaid())
                .eventDate(event.getEventDate())
                .publishedOn(event.getPublishedOn())
                .createdOn(event.getCreatedOn())
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();
    }
}
