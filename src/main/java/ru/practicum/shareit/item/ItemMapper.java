package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class})
public interface ItemMapper {

    @Mapping(target = "owner", ignore = true)
    Item toEntity(ChangeItemDto changeItemDto);

    @Mapping(target = "ownerName", source = "owner.name")
    ItemResponseDto toItemDto(Item item);

    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "lastBooking", source = "lastBooking")
    ItemDtoWithBookings toDtoWithBookings(Item item);

    ItemDtoSimple toItemDtoSimple(Item item);

    List<ItemResponseDto> toItemDtoList(List<Item> item);
}
