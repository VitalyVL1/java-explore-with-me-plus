package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.compilation.CompilationEvent;
import ru.practicum.model.compilation.EventCompilationId;

import java.util.List;

/**
 * Репозиторий для выполнения операций с сущностью {@link CompilationEvent}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также дополнительные методы для управления связями между подборками и событиями:
 * удаление всех связей подборки и получение событий для списка подборок.
 * </p>
 *
 * @see CompilationEvent
 * @see EventCompilationId
 */
public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    /**
     * Удаляет все связи для указанной подборки.
     * <p>
     * Используется при обновлении списка событий в подборке: старые связи удаляются,
     * затем создаются новые. Метод транзакционный и выполняется в рамках одной транзакции.
     * </p>
     *
     * @param compilationId идентификатор подборки, связи которой нужно удалить
     */
    @Transactional
    @Modifying
    @Query("""
            delete
              from CompilationEvent ce
             where ce.id in (select ce2.id
                               from CompilationEvent ce2
                                    join ce2.compilation as c
                              where c.id = :compId)
            """)
    void deleteByCompilationId(@Param("compId") long compilationId);

    /**
     * Возвращает список объектов {@link EventCompilationId} для указанных подборок.
     * <p>
     * Используется для эффективного получения всех событий, входящих в список подборок,
     * с группировкой по идентификаторам подборок. Результат содержит пары
     * (идентификатор подборки, сущность события).
     * </p>
     *
     * @param compilationIds список идентификаторов подборок
     * @return список объектов {@link EventCompilationId} с информацией о связях
     */
    @Query("""
            select new ru.practicum.model.compilation.EventCompilationId(c.id, ce.event)
              from CompilationEvent as ce
                   join ce.compilation as c
             where c.id in (:compIds)
            """)
    List<EventCompilationId> getEventsByCompilationIds(@Param("compIds") List<Long> compilationIds);
}