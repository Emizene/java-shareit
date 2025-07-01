package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toEntity(ChangeItemDto changeItemDto);

    ItemResponseDto toItemDto(Item item);

    List<ItemResponseDto> toItemDtoList(List<Item> item);
}
