package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Data
public class BookItemRequestDto {
    private Long itemId;
    @FutureOrPresent(message = "Дата начала должна быть сегодня или позже")
    private LocalDateTime start;

    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
}