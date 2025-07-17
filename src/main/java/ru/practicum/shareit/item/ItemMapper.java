package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public Item toEntity(ChangeItemDto changeItemDto) {
        return Item.builder()
                .id(changeItemDto.getId())
                .name(changeItemDto.getName())
                .description(changeItemDto.getDescription())
                .available(changeItemDto.getAvailable())
                .build();
    }

    public ItemResponseDto toItemDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerName(item.getOwner() != null ? item.getOwner().getName() : null)
                .comments(mapComments(item.getComments()))
                .build();
    }

    public ItemDtoWithBookings toDtoWithBookings(Item item) {
        return ItemDtoWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .nextBooking(mapNextBooking(item))
                .lastBooking(mapLastBooking(item))
                .comments(mapComments(item.getComments()))
                .ownerName(item.getOwner() != null ? item.getOwner().getName() : null)
                .build();
    }

    public ItemDtoSimple toItemDtoSimple(Item item) {
        return ItemDtoSimple.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public List<ItemResponseDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    public BookingDtoSimple mapNextBooking(Item item) {
        return item.getBookings().stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(bookingMapper::toBookingDtoSimple).orElse(null);
    }

    public BookingDtoSimple mapLastBooking(Item item) {
        return item.getBookings().stream()
                .filter(booking -> booking.getEnd().toLocalDate().isBefore(LocalDate.now()))
                .max(Comparator.comparing(Booking::getStart)).map(bookingMapper::toBookingDtoSimple)
                .orElse(null);
    }

    private List<CommentResponseDto> mapComments(Set<Comment> comments) {
        if (comments == null) return Collections.emptyList();
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
