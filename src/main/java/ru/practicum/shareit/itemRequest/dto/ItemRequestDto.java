package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
}