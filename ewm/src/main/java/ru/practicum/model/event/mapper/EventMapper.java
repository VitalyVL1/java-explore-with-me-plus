package ru.practicum.model.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.mapper.CategoryMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.user.User;

@Mapper(componentModel = "spring",
        imports = {ru.practicum.model.category.mapper.CategoryMapper.class,
                ru.practicum.model.user.mapper.UserMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    Event toEntity(NewEventDto dto, User initiator, Category category, State state);
import ru.practicum.model.user.mapper.UserMapper;

    default Event toEntity(NewEventDto dto, User initiator, Category category) {
        return toEntity(dto, initiator, category, State.PENDING);
public class EventMapper {
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

    @Mapping(target = "category", expression = "java(CategoryMapper.mapToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(UserMapper.mapToUserShortDto(event.getInitiator()))")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "category", expression = "java(CategoryMapper.mapToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(UserMapper.mapToUserShortDto(event.getInitiator()))")
    EventShortDto toShortDto(Event event);
}
