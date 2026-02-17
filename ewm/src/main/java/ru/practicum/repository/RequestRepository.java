package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для выполнения операций с сущностью {@link Request}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также специализированные методы для работы с заявками на участие в событиях.
 * Включает методы с оптимизированной загрузкой связанных сущностей через JOIN FETCH
 * и EntityGraph для предотвращения проблемы N+1 запросов.
 * </p>
 *
 * @see Request
 * @see JpaRepository
 * @see EntityGraph
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Находит все заявки указанного пользователя с загрузкой данных о заявителе.
     * <p>
     * Использует JOIN FETCH для предотвращения ленивой загрузки и оптимизации производительности.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @return список заявок пользователя
     */
    @Query("""
            select r
              from Request r
                   join fetch r.requester as u
             where u.id = :userId
            """)
    List<Request> findUserRequests(@Param("userId") long userId);

    /**
     * Находит заявку конкретного пользователя на конкретное событие.
     * <p>
     * Использует JOIN FETCH для загрузки данных о заявителе и событии.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return Optional с заявкой, если она существует
     */
    @Query("""
            select r
              from Request r
                   join fetch r.requester as u
                   join fetch r.event as e
             where u.id = :userId
               and e.id = :eventId
            """)
    Optional<Request> findByUserAndEvent(@Param("userId") long userId, @Param("eventId") long eventId);

    /**
     * Подсчитывает количество заявок на событие с определенным статусом.
     *
     * @param eventId идентификатор события
     * @param status статус заявки
     * @return количество заявок с указанным статусом
     */
    @Query("""
            select count(r)
              from Request r
                   join r.event as e
             where e.id = :eventId
               and r.status = :status
            """)
    long countByEventAndStatus(@Param("eventId") long eventId, @Param("status") RequestStatus status);

    /**
     * Находит все заявки на указанное событие для его инициатора.
     * <p>
     * Использует {@link EntityGraph} для загрузки связанной сущности event
     * и предотвращения проблемы N+1 запросов.
     * </p>
     *
     * @param eventId идентификатор события
     * @param userId идентификатор инициатора события
     * @return список заявок на событие
     */
    @EntityGraph(attributePaths = "event")
    List<Request> findByEvent_IdAndEvent_Initiator_Id(Long eventId, Long userId);

    /**
     * Подсчитывает количество подтвержденных заявок для каждого события из списка.
     * <p>
     * Возвращает массив объектов, где каждый элемент содержит:
     * - [0] идентификатор события
     * - [1] количество подтвержденных заявок
     * </p>
     *
     * @param eventIds список идентификаторов событий
     * @return список массивов с парами (id события, количество подтвержденных заявок)
     */
    @Query("""
            SELECT r.event.id, COUNT(r)
            FROM Request r
            WHERE r.event.id IN :eventIds AND r.status = 'CONFIRMED'
            GROUP BY r.event.id
            """)
    List<Object[]> countConfirmedRequestsByEventIds(@Param("eventIds") List<Long> eventIds);
}