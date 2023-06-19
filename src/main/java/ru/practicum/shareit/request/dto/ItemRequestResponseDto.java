package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ItemRequestResponseDto {
    @NotNull
    private Long id;
    @NotBlank
    private String description;
    @NotNull
    private LocalDateTime created;
}
