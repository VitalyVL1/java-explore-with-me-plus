package ru.practicum.dto;

import lombok.Builder;

import java.util.List;

public record EwmRequestStats(
        String start,
        String end,
        List<String> uris,
        Boolean unique
) {
    @Builder(toBuilder = true)
    public EwmRequestStats {
    }
}
