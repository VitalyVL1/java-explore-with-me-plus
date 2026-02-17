package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для выполнения операций с сущностью {@link Stat}.
 * <p>
 * Предоставляет стандартные CRUD операции через наследование от {@link JpaRepository},
 * а также специализированные методы для получения статистики по просмотрам
 * с поддержкой фильтрации по диапазону дат, списку URI и режиму уникальности.
 * </p>
 *
 * @see Stat
 * @see JpaRepository
 * @see ResponseStatsDto
 */
public interface StatsRepository extends JpaRepository<Stat, Long> {

    /**
     * Возвращает статистику по всем просмотрам (включая повторные с одного IP).
     * <p>
     * Группирует записи по приложению и URI, подсчитывает общее количество просмотров
     * (все IP, включая повторные) за указанный период, с возможностью фильтрации по URI.
     * Результат сортируется по убыванию количества просмотров.
     * </p>
     *
     * @param start начало периода
     * @param end конец периода
     * @param uris список URI для фильтрации (если null или пустой - фильтр не применяется)
     * @return список DTO с данными статистики (app, uri, hits)
     */
    @Query("select new ru.practicum.dto.ResponseStatsDto(st.app, st.uri, count(st.ip)) " +
           "from Stat st " +
           "where st.timestamp between :start and :end " +
           "and (:uris is null or st.uri in :uris) " +
           "group by st.app, st.uri " +
           "order by count(st.ip) desc")
    List<ResponseStatsDto> findStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    /**
     * Возвращает статистику по уникальным просмотрам (учитывает только уникальные IP).
     * <p>
     * Группирует записи по приложению и URI, подсчитывает количество уникальных IP-адресов
     * за указанный период, с возможностью фильтрации по URI.
     * Результат сортируется по убыванию количества уникальных просмотров.
     * </p>
     *
     * @param start начало периода
     * @param end конец периода
     * @param uris список URI для фильтрации (если null или пустой - фильтр не применяется)
     * @return список DTO с данными статистики (app, uri, hits) с уникальными просмотрами
     */
    @Query("select new ru.practicum.dto.ResponseStatsDto(st.app, st.uri, count(distinct st.ip)) " +
           "from Stat st " +
           "where st.timestamp between :start and :end " +
           "and (:uris is null or st.uri in :uris) " +
           "group by st.app, st.uri " +
           "order by count(distinct st.ip) desc")
    List<ResponseStatsDto> findUniqueStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}