package item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void testSuccessCreateItem() {
        Long userId = 1L;
        ChangeItemDto changeItemDto = new ChangeItemDto();
        changeItemDto.setName("Item");
        changeItemDto.setDescription("Description");
        changeItemDto.setAvailable(true);

        ItemResponseDto expectedItemDto = new ItemResponseDto();
        expectedItemDto.setId(1L);
        expectedItemDto.setName("Item");

        when(itemService.createItem(changeItemDto, userId)).thenReturn(ResponseEntity.ok(expectedItemDto));

        ItemResponseDto result = itemController.createItem(changeItemDto, userId).getBody();

        assertEquals(expectedItemDto, result);
        verify(itemService).createItem(changeItemDto, userId);
    }

    @Test
    void testSuccessUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ChangeItemDto updateForItem = new ChangeItemDto();
        updateForItem.setName("Updated Name");

        ItemResponseDto expectedItem = new ItemResponseDto();
        expectedItem.setId(itemId);
        expectedItem.setName("Updated Name");

        when(itemService.updateItem(itemId, updateForItem, userId)).thenReturn(ResponseEntity.ok(expectedItem));

        ItemResponseDto result = itemController.updateItem(updateForItem, userId, itemId).getBody();

        assertEquals(expectedItem, result);
        verify(itemService).updateItem(itemId, updateForItem, userId);
    }

    @Test
    void testSuccessSearchItems() {
        String searchText = "test";
        ItemResponseDto itemDto = new ItemResponseDto();
        itemDto.setName("Test Item");
        List<ItemResponseDto> expectedItems = List.of(itemDto);

        when(itemService.searchItem(searchText)).thenReturn(ResponseEntity.ok(expectedItems));

        Collection<ItemResponseDto> result = itemController.searchItems(searchText).getBody();

        assertEquals(expectedItems, result);
        verify(itemService).searchItem(searchText);
    }

    @Test
    void testSuccessGetItemById() {
        Long itemId = 1L;
        ItemDtoWithBookings expectedItem = new ItemDtoWithBookings();
        expectedItem.setId(itemId);

        when(itemService.getItemById(itemId)).thenReturn(ResponseEntity.ok(expectedItem));

        ItemDtoWithBookings result = itemController.getItemById(itemId).getBody();

        assertEquals(expectedItem, result);
        verify(itemService).getItemById(itemId);
    }

    @Test
    void testSuccessGetItemsByOwner() {
        Long userId = 1L;

        ItemDtoWithBookings itemDto = new ItemDtoWithBookings();
        List<ItemDtoWithBookings> expectedItems = List.of(itemDto);

        when(itemService.getItemsByOwner(userId)).thenReturn(ResponseEntity.ok(expectedItems));

        Collection<ItemDtoWithBookings> result = itemController.getItemsByOwner(userId).getBody();

        assertEquals(expectedItems, result);
        verify(itemService).getItemsByOwner(userId);
    }

    @Test
    void testSuccessAddComment() {
        Long userId = 1L;
        Long itemId = 1L;
        ChangeCommentDto newCommentRequest = new ChangeCommentDto();
        newCommentRequest.setText("Comment");

        CommentResponseDto expectedCommentDto = new CommentResponseDto();
        expectedCommentDto.setText("Comment");

        when(itemService.addComment(itemId, newCommentRequest, userId)).thenReturn(ResponseEntity.ok(expectedCommentDto));

        CommentResponseDto result = itemController.addComment(itemId, newCommentRequest, userId).getBody();

        assertEquals(expectedCommentDto, result);
        verify(itemService).addComment(itemId, newCommentRequest, userId);
    }

    @Test
    void testSuccessDeleteItemById() {
        Long itemId = 1L;

        when(itemService.deleteItemById(itemId))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = itemController.deleteItemById(itemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemService).deleteItemById(itemId);
    }
}