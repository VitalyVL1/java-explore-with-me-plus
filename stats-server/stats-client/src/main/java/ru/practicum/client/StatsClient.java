package ru.practicum.client;

import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStats;
import ru.practicum.dto.ResponseStatsDto;

import java.util.List;

public interface StatsClient {
    void hit(HitCreateDto dto);

    List<ResponseStatsDto> get(RequestStats request);
}
