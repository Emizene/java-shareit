package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Valid
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ChangeItemDto item,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@Valid @RequestBody ChangeItemDto item,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestParam(name = "text") String searchText,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItem(searchText);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long itemId) {
        return itemService.deleteItemById(itemId);
    }
}
