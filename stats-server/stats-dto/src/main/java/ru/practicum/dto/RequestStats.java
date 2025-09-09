package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record RequestStats(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime start,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime end,
        List<String> uris,
        Boolean unique
) {
    @Builder(toBuilder = true)
    public RequestStats {
    }
}
