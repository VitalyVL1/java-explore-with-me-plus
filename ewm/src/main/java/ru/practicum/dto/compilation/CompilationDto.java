package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private boolean pinned;
    @JsonProperty("events")
    private Set<Long> eventIds; // ToDo переделать когда будет EventShortDTO
}
