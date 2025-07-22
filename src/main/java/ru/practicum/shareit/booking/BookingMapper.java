package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class BookingMapper {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Mapping(target = "item", expression = "java(itemRepository.findById(changeBookingDto.getItemId()).orElse(null))")
    @Mapping(target = "id", ignore = true)
    public abstract Booking toEntity(ChangeBookingDto changeBookingDto);

    @Mapping(target = "booker", source = "booker")
    public abstract BookingResponseDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker() != null ? booking.getBooker().getId() : null)")
    public abstract BookingDtoSimple toBookingDtoSimple(Booking booking);

    public abstract List<BookingResponseDto> toBookingDtoList(List<Booking> booking);
}
