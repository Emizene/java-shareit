package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestResponseWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();

    }

    public ItemRequestResponseDto toDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestResponseDto toDtoWithItems(ItemRequest itemRequest, List<Item> item) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(item.stream().map(itemMapper::toItemDtoSimple).collect(Collectors.toList()))
                .build();
    }

    public List<ItemRequestResponseDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
