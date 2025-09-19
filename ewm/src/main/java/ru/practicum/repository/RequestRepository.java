package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.request.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long>  {

    @Query("""
            select r
              from Request r
                   join fetch r.requester as u
             where u.id = :userId
            """)
    List<Request> findUserRequests(@Param("userId") long userId);

    @Query("""
            select r
              from Request r
                   join fetch r.requester as u
                   join fetch r.event as e
             where u.id = :userId
               and e.id = :eventId
            """)
    Optional<Request> findByUserAndEvent(@Param("userId") long userId, @Param("eventId") long eventId);

    @Query("""
            select count(r)
              from Request r
                   join r.event as e
             where e.id = :eventId
               and r.status = :status
            """)
    int countByEventAndStatus(@Param("eventId") long eventId, @Param("status") String status);
}
