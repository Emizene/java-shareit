package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ChangeUserDto {
    private Long id;
    @Email(message = "Некорректный формат")
    private String email;
    private String name;
}

