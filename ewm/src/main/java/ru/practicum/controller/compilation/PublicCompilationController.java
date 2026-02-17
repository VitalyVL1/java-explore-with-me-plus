package ru.practicum.controller.compilation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

/**
 * REST-контроллер для публичного доступа к подборкам событий.
 * <p>
 * Предоставляет endpoints для получения списка всех подборок с возможностью фильтрации
 * по признаку закрепления и пагинации, а также для получения информации о конкретной подборке
 * по её идентификатору. Доступен всем пользователям без необходимости аутентификации.
 * </p>
 *
 * @see CompilationService
 * @see CompilationDto
 */
@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    /**
     * Возвращает список подборок событий с возможностью фильтрации и пагинации.
     * <p>
     * Позволяет получить все подборки событий с возможностью фильтрации по признаку
     * закрепления на главной странице (pinned) и постраничного вывода.
     * </p>
     *
     * @param pinned фильтр по признаку закрепления подборки (необязательный параметр):
     *               true - только закрепленные, false - только незакрепленные, null - все подборки
     * @param from индекс первого элемента для пагинации (по умолчанию 0, должен быть неотрицательным)
     * @param size количество элементов на странице (по умолчанию 10, должен быть положительным)
     * @return список DTO подборок событий
     * @throws jakarta.validation.ConstraintViolationException если параметры пагинации не соответствуют ограничениям
     */
    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false, name = "pinned") Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Public: getting compilations with pinned={}, from={}, size={}", pinned, from, size);
        return compilationService.findCompilationsByParam(pinned, pageable);
    }

    /**
     * Возвращает информацию о подборке по её идентификатору.
     * <p>
     * Позволяет получить детальную информацию о конкретной подборке событий,
     * включая список всех событий, входящих в неё.
     * </p>
     *
     * @param compilationId идентификатор подборки
     * @return DTO подборки с указанным идентификатором
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") long compilationId) {
        log.info("Public: get compilation id={}", compilationId);
        return compilationService.findCompilationById(compilationId);
    }
}