package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Autowired
    private BookingMapper bookingMapper;

    private final LocalDateTime now = LocalDateTime.now();
    private final User booker = new User(1L, "Booker", "booker@yandex.ru");
    private final Item item = new Item(1L, "Item", "Description", true, new User(2L, "Owner", "owner@yandex.ru"), null);

    @Test
    void testToBookingDto_shouldMapEntityToResponseDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        BookingResponseDto dto = bookingMapper.toBookingDto(booking);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", booking.getStart())
                .hasFieldOrPropertyWithValue("end", booking.getEnd())
                .hasFieldOrPropertyWithValue("status", Status.APPROVED)
                .hasFieldOrProperty("item")
                .hasFieldOrProperty("booker");
    }

    @Test
    void testToBookingDtoSimple_shouldMapEntityToSimpleDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .build();

        BookingDtoSimple dto = bookingMapper.toBookingDtoSimple(booking);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", 1L);
    }

    @Test
    void testToBookingDtoSimple_shouldHandleNullBooker() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(null)
                .build();

        BookingDtoSimple dto = bookingMapper.toBookingDtoSimple(booking);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("bookerId", null);
    }

    @Test
    void testToBookingDtoList_shouldMapListOfEntities() {
        List<Booking> bookings = List.of(
                Booking.builder().id(1L).booker(booker).build(),
                Booking.builder().id(2L).booker(booker).build()
        );

        List<BookingResponseDto> dto = bookingMapper.toBookingDtoList(bookings);

        assertThat(dto)
                .hasSize(2)
                .extracting(BookingResponseDto::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void testToEntity_shouldReturnNullWhenInputIsNull() {
        assertThat(bookingMapper.toEntity(null)).isNull();
    }
}
