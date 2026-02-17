package ru.practicum.service.compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.List;

/**
 * Сервис для управления подборками событий.
 * <p>
 * Определяет бизнес-логику для работы с подборками: создание, обновление,
 * удаление, поиск по параметрам и по идентификатору. Подборки представляют собой
 * тематические группы событий, которые могут быть закреплены на главной странице.
 * </p>
 *
 * @see ru.practicum.controller.compilation.AdminCompilationController
 * @see ru.practicum.controller.compilation.PublicCompilationController
 * @see CompilationDto
 * @see NewCompilationDto
 * @see UpdateCompilationRequest
 */
public interface CompilationService {

    /**
     * Создает новую подборку событий.
     *
     * @param compilationDto DTO с данными новой подборки (заголовок, признак закрепления, список событий)
     * @return DTO созданной подборки с присвоенным идентификатором
     * @throws ru.practicum.exception.NotFoundException если любое из указанных событий не найдено
     */
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    /**
     * Удаляет подборку событий.
     *
     * @param compilationId идентификатор удаляемой подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    void deleteCompilation(long compilationId);

    /**
     * Обновляет существующую подборку.
     * <p>
     * Может обновлять заголовок, признак закрепления и список событий.
     * </p>
     *
     * @param compilationId идентификатор обновляемой подборки
     * @param compilationDto DTO с обновленными данными (все поля опциональны)
     * @return DTO обновленной подборки
     * @throws ru.practicum.exception.NotFoundException если подборка не найдена или любое из указанных событий не найдено
     */
    CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest compilationDto);

    /**
     * Находит подборки по параметрам с пагинацией.
     * <p>
     * Позволяет фильтровать подборки по признаку закрепления на главной странице.
     * </p>
     *
     * @param pinned фильтр по признаку закрепления (true - только закрепленные,
     *               false - только незакрепленные, null - все подборки)
     * @param pageable параметры пагинации
     * @return список DTO подборок, соответствующих параметрам
     */
    List<CompilationDto> findCompilationsByParam(Boolean pinned, Pageable pageable);

    /**
     * Находит подборку по идентификатору.
     *
     * @param compilationId идентификатор подборки
     * @return DTO найденной подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    CompilationDto findCompilationById(long compilationId);
}