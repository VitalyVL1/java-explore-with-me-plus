package ru.practicum.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

/**
 * REST-контроллер для административного управления подборками событий.
 * <p>
 * Предоставляет endpoints для создания, обновления и удаления подборок событий.
 * Подборки могут содержать список событий и использоваться для тематической группировки
 * на главной странице. Доступен только пользователям с ролью администратора.
 * </p>
 *
 * @see CompilationService
 * @see CompilationDto
 * @see NewCompilationDto
 * @see UpdateCompilationRequest
 */
@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    /**
     * Создает новую подборку событий.
     * <p>
     * Позволяет администратору создать подборку с указанием заголовка,
     * флага закрепления на главной странице и списка событий.
     * </p>
     *
     * @param compilationDto DTO с данными новой подборки
     * @return DTO созданной подборки с присвоенным идентификатором
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Admin: creating new compilation title={}", compilationDto.getTitle());
        return compilationService.createCompilation(compilationDto);
    }

    /**
     * Удаляет подборку событий.
     * <p>
     * Удаляет подборку по её идентификатору. При удалении подборки сами события не удаляются.
     * </p>
     *
     * @param compilationId идентификатор удаляемой подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") long compilationId) {
        log.info("Admin: deleting compilation id={}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

    /**
     * Обновляет существующую подборку событий.
     * <p>
     * Позволяет администратору изменить заголовок подборки, флаг закрепления
     * или список событий в ней.
     * </p>
     *
     * @param compilationUpdateDto DTO с обновленными данными подборки
     * @param compilationId идентификатор обновляемой подборки
     * @return DTO обновленной подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     */
    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationRequest compilationUpdateDto,
                                            @PathVariable("compId") long compilationId) {
        log.info("Admin: updating compilation id={}", compilationId);
        return compilationService.updateCompilation(compilationId, compilationUpdateDto);
    }
}