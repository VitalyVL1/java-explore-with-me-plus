package ru.practicum.model.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.user.User;

/**
 * Маппер для преобразования между сущностью события и различными DTO.
 * <p>
 * Использует MapStruct для автоматической генерации кода преобразования.
 * Содержит сложные маппинги для вложенных объектов (категория, инициатор)
 * и преобразования между разными представлениями события.
 * </p>
 *
 * @see Event
 * @see EventFullDto
 * @see EventShortDto
 * @see NewEventDto
 */
@Mapper(componentModel = "spring",
        imports = {ru.practicum.model.category.mapper.CategoryMapper.class,
                ru.practicum.model.user.mapper.UserMapper.class})
public interface EventMapper {

    /**
     * Преобразует DTO создания события в сущность события.
     * <p>
     * Маппинг полей:
     * <ul>
     *   <li>id игнорируется (генерируется БД)</li>
     *   <li>initiator, category, state устанавливаются из параметров</li>
     *   <li>publishedOn, createdOn, views, confirmedRequests игнорируются</li>
     * </ul>
     * </p>
     *
     * @param dto DTO с данными нового события
     * @param initiator инициатор события (пользователь)
     * @param category категория события
     * @param state статус события (по умолчанию PENDING)
     * @return сущность события, готовая для сохранения в БД
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    Event toEntity(NewEventDto dto, User initiator, Category category, State state);

    /**
     * Перегрузка метода для создания события со статусом PENDING по умолчанию.
     *
     * @param dto DTO с данными нового события
     * @param initiator инициатор события
     * @param category категория события
     * @return сущность события со статусом PENDING
     */
    default Event toEntity(NewEventDto dto, User initiator, Category category) {
        return toEntity(dto, initiator, category, State.PENDING);
    }

    /**
     * Преобразует сущность события в DTO с полной информацией.
     * <p>
     * Выполняет сложное преобразование вложенных объектов:
     * <ul>
     *   <li>category преобразуется с помощью {@link ru.practicum.model.category.mapper.CategoryMapper}</li>
     *   <li>initiator преобразуется с помощью {@link ru.practicum.model.user.mapper.UserMapper}</li>
     * </ul>
     * </p>
     *
     * @param event сущность события
     * @return DTO события с полной информацией
     */
    @Mapping(target = "category", expression = "java(CategoryMapper.mapToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(UserMapper.mapToUserShortDto(event.getInitiator()))")
    EventFullDto toFullDto(Event event);

    /**
     * Преобразует сущность события в DTO с краткой информацией.
     * <p>
     * Выполняет сложное преобразование вложенных объектов:
     * <ul>
     *   <li>category преобразуется с помощью {@link ru.practicum.model.category.mapper.CategoryMapper}</li>
     *   <li>initiator преобразуется с помощью {@link ru.practicum.model.user.mapper.UserMapper}</li>
     * </ul>
     * </p>
     *
     * @param event сущность события
     * @return DTO события с краткой информацией
     */
    @Mapping(target = "category", expression = "java(CategoryMapper.mapToCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(UserMapper.mapToUserShortDto(event.getInitiator()))")
    EventShortDto toShortDto(Event event);
}