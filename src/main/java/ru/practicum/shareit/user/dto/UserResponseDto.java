package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
}
