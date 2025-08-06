package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.SharedHeaders;
import ru.practicum.shareit.item.dto.comment.ChangeCommentDto;
import ru.practicum.shareit.item.dto.ChangeItemDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Valid
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                             @Valid @RequestBody ChangeItemDto item) {
        log.info("Create item {} , userId={}", item, userId);
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId, @PathVariable Long itemId,
                                             @Valid @RequestBody ChangeItemDto item) {
        log.info("Update item {}, itemId={} , userId={}", item, itemId, userId);
        return itemClient.updateItem(userId, itemId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                              @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        log.info("Get searched item with text={}, userId={}", text, userId);
        return itemClient.searchItems(userId, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Get item with userId={}, itemId={}", userId, itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        log.info("Get item for owner userId={}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId, @Valid @RequestBody ChangeCommentDto comment) {
        log.info("Post new comment {}, userId={}", comment, userId);
        return itemClient.addComment(userId, itemId, comment);
    }
}

