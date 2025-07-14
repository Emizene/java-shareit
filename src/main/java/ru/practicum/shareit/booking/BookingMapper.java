package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
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
//    @Mapping(target = "booker", source = "booker")
    public abstract Booking toEntity(ChangeBookingDto changeBookingDto);

    //    @Mapping(target = "bookerId", source = "booker.id")

    @Mapping(target = "booker", source = "booker")
//    @Mapping(target = "booker", source = "booker.id")
//    @Mapping(target = "item", source = "item.id")
    public abstract BookingResponseDto toBookingDto(Booking booking);

    public abstract List<BookingResponseDto> toBookingDtoList(List<Booking> booking);
}
