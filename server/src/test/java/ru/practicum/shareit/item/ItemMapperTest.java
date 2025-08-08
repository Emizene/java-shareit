package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.Status;
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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemMapper itemMapper;

    private final LocalDateTime now = LocalDateTime.now();
    private final User owner = new User(1L, "Owner", "owner@yandex.ru");
    private final User booker = new User(2L, "Booker", "booker@yandex.ru");
    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(owner)
            .build();

    @Test
    void testToEntity_shouldMapChangeItemDtoToEntity() {
        ChangeItemDto dto = ChangeItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        Item entity = itemMapper.toEntity(dto);

        assertThat(entity)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Item")
                .hasFieldOrPropertyWithValue("description", "Description")
                .hasFieldOrPropertyWithValue("available", true);
    }

    @Test
    void testToItemDto_shouldMapItemToResponseDto() {
        item.setComments(Set.of(new Comment()));

        when(commentMapper.toCommentDto(any())).thenReturn(new CommentResponseDto());

        ItemResponseDto dto = itemMapper.toItemDto(item);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Item")
                .hasFieldOrPropertyWithValue("description", "Description")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("ownerName", "Owner");

        assertThat(dto.getComments()).hasSize(1);
        verify(commentMapper).toCommentDto(any());
    }

    @Test
    void testToItemDtoSimple_shouldMapItemToSimpleDto() {
        ItemDtoSimple dto = itemMapper.toItemDtoSimple(item);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Item");
    }

    @Test
    void toDtoWithBookings_shouldMapItemToDtoWithBookings() {
        Booking nextBooking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        Booking lastBooking = Booking.builder()
                .id(2L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        item.setBookings(Set.of(nextBooking, lastBooking));
        item.setComments(Set.of(new Comment()));

        when(bookingMapper.toBookingDtoSimple(nextBooking))
                .thenReturn(new BookingDtoSimple(1L, now.plusDays(1), now.plusDays(2), Status.APPROVED, booker.getId()));

        when(bookingMapper.toBookingDtoSimple(lastBooking))
                .thenReturn(new BookingDtoSimple(2L, now.minusDays(2), now.minusDays(1), Status.APPROVED, booker.getId()));

        when(commentMapper.toCommentDto(any())).thenReturn(new CommentResponseDto());

        ItemDtoWithBookings dto = itemMapper.toDtoWithBookings(item);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Item")
                .hasFieldOrPropertyWithValue("description", "Description")
                .hasFieldOrPropertyWithValue("available", true);

        assertThat(dto.getNextBooking())
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", booker.getId());

        assertThat(dto.getLastBooking())
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("bookerId", booker.getId());

        verify(bookingMapper, times(2)).toBookingDtoSimple(any());
    }

    @Test
    void toItemDtoSimple_shouldMapItemToSimpleDto() {
        ItemDtoSimple dto = itemMapper.toItemDtoSimple(item);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Item");
    }

    @Test
    void mapNextBooking_shouldReturnCorrectNextBooking() {
        Booking nextBooking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        item.setBookings(Set.of(nextBooking));

        when(bookingMapper.toBookingDtoSimple(nextBooking))
                .thenReturn(new BookingDtoSimple(1L, now.plusDays(1), now.plusDays(2), Status.APPROVED, booker.getId()));

        BookingDtoSimple result = itemMapper.mapNextBooking(item);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", now.plusDays(1))
                .hasFieldOrPropertyWithValue("bookerId", booker.getId());
    }

    @Test
    void mapLastBooking_shouldReturnCorrectLastBooking() {
        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        item.setBookings(Set.of(lastBooking));

        when(bookingMapper.toBookingDtoSimple(lastBooking))
                .thenReturn(new BookingDtoSimple(1L, now.minusDays(2), now.minusDays(1), Status.APPROVED, booker.getId()));

        BookingDtoSimple result = itemMapper.mapLastBooking(item);

        assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("end", now.minusDays(1))
                .hasFieldOrPropertyWithValue("bookerId", booker.getId());
    }
}