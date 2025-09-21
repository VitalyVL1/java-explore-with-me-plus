package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NewCompilationDto {
    private Boolean pinned;
    private String title;
    @JsonProperty("events")
    private Set<Long> eventIds;
}
