package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.SharedHeaders;
import ru.practicum.shareit.item.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ChangeItemDto item,
                                                      @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@Valid @RequestBody ChangeItemDto item,
                                                      @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                                      @PathVariable Long itemId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestParam(name = "text") String searchText) {
        return itemService.searchItem(searchText);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoWithBookings> getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoWithBookings>> getItemsByOwner(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long itemId, @Valid @RequestBody ChangeCommentDto comment,
                                                         @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        return itemService.addComment(itemId, comment, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItemById(@PathVariable Long itemId) {
        return itemService.deleteItemById(itemId);
    }
}
