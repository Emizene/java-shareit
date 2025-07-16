package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {

    ResponseEntity<ItemResponseDto> createItem(ChangeItemDto item, Long userId);

    ResponseEntity<ItemResponseDto> updateItem(Long itemId, ChangeItemDto item, Long userId);

    ResponseEntity<ItemDtoWithBookings> getItemById(Long itemId);

    ResponseEntity<List<ItemDtoWithBookings>> getItemsByOwner(Long userId);

    ResponseEntity<List<ItemResponseDto>> searchItem(String searchText);

    ResponseEntity<Void> deleteItemById(Long itemId);

    ResponseEntity<CommentResponseDto> addComment(Long itemId, ChangeCommentDto comment, Long userId);
}
