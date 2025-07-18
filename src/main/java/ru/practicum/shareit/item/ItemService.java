package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ResponseEntity<ItemResponseDto> createItem(ChangeItemDto item, Long userId);

    ResponseEntity<ItemResponseDto> updateItem(Long itemId, ChangeItemDto item, Long userId);

    ResponseEntity<ItemResponseDto> getItemById(Long itemId);

    ResponseEntity<List<ItemResponseDto>> searchItem(String searchText);

    ResponseEntity<Void> deleteItemById(Long itemId);

    ResponseEntity<List<ItemResponseDto>> getAllUserItems(Long userId);
}
