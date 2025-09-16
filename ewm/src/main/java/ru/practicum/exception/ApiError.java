package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String status,
        String reason,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        List<String> errors
) {
    public ApiError(String status, String reason, String message) {
        this(status, reason, message, LocalDateTime.now(), null);
    }

    public ApiError(String status, String reason, String message, List<String> errors) {
        this(status, reason, message, LocalDateTime.now(), errors);
    }
}