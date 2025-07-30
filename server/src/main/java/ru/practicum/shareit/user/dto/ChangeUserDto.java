package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class ChangeUserDto {
    private Long id;
    private String email;
    private String name;
}
