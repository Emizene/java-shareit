package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring", uses = {BookingMapper.class})
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

    private BookingDtoSimple mapNextBooking(Item item) {
        return item.getBookings().stream()
                .filter(booking -> booking.getStart().isAfter(ChronoLocalDateTime.from(Instant.now())))
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .findFirst()
                .map(bookingMapper::toBookingDtoSimple)
                .orElse(null);
    }

    private BookingDtoSimple mapLastBooking(Item item) {
        return item.getBookings().stream()
                .filter(booking -> booking.getEnd().isBefore(ChronoLocalDateTime.from(Instant.now())))
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .findFirst()
                .map(bookingMapper::toBookingDtoSimple)
                .orElse(null);
    }

    private List<CommentResponseDto> mapComments(Set<Comment> comments) {
        if (comments == null) return Collections.emptyList();
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}

//    @Mapping(target = "owner", ignore = true)
//    Item toEntity(ChangeItemDto changeItemDto);
//
//    @Mapping(target = "ownerName", source = "owner.name")
//    ItemResponseDto toItemDto(Item item);
//
//    @Mapping(target = "nextBooking", source = "nextBooking")
//    @Mapping(target = "lastBooking", source = "lastBooking")
//    ItemDtoWithBookings toDtoWithBookings(Item item);
//
//    ItemDtoSimple toItemDtoSimple(Item item);
//
//    List<ItemResponseDto> toItemDtoList(List<Item> item);
//}
