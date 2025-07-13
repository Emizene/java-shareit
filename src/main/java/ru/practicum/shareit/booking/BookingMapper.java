package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toEntity(ChangeBookingDto changeBookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingResponseDto toBookingDto(Booking booking);

    List<BookingResponseDto> toBookingDtoList(List<Booking> booking);
}
