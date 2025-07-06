package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", ignore = true)
    Item toEntity(ChangeItemDto changeItemDto);

    @Mapping(target = "ownerName", source = "owner.name")
    ItemResponseDto toItemDto(Item item);


    List<ItemResponseDto> toItemDtoList(List<Item> item);
}
