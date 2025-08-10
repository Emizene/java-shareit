package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;

    private static final Long ID = 1L;
    private final Long otherUser = 2L;
    private final User user = new User(ID, "User", "ru.practicum.shareit.user@yandex.ru");
    private final User notOwner = new User(2L, "User2", "user2@yandex.ru");
    private final ChangeItemDto changeItemDto = new ChangeItemDto(ID, "Item", "Description", true, null);
    private final Item item = new Item(ID, "Item", "Description", true, user, null);
    private final String searchText = "test";
    private final List<Item> testItems = List.of(
            new Item(1L, "Test Item 1", "Description 1", true, user, null),
            new Item(2L, "Test Item 2", "Description 2", true, user, null)
    );

    @Test
    void testSuccessCreateItem_whenUserFound_thenSavedItem() {
        ItemResponseDto itemResponseDto = new ItemResponseDto(null, "Item", "Description", true, "User", null);
        ChangeItemDto requestDto = new ChangeItemDto(null, "Item", "Description", true, null);
        Item newItem = new Item(null, "Item", "Description", true, user, null);
        Item savedItem = new Item(ID, "Item", "Description", true, user, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemMapper.toEntity(any())).thenReturn(newItem);
        when(itemRepository.save(any())).thenReturn(savedItem);
        when(itemMapper.toItemDto(any())).thenReturn(itemResponseDto);

        var actualItemDto = itemService.createItem(requestDto, ID);

        verify(userRepository).findById(anyLong());
        verify(itemMapper).toEntity(any());
        verify(itemRepository).save(any());
        verify(itemMapper).toItemDto(any());

        Assertions.assertNotNull(actualItemDto);
        Assertions.assertEquals(ResponseEntity.status(201).body(itemResponseDto), actualItemDto);
    }

    @Test
    void testCreateItem_whenUserNotFound_thenNotSavedItem() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(changeItemDto, 2L));
    }

    @Test
    void testSuccessUpdateItem_whenUserIsOwner_thenUpdatedItem() {
        Long userId = ID;
        Long itemId = ID;
        User owner = new User(userId, "Owner", "owner@mail.ru");
        Item existingItem = new Item(itemId, "Old Name", "Old Desc", true, owner, null);
        ItemResponseDto expectedDto = new ItemResponseDto(itemId, "Item", "Description", true, "Owner", null);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);
        when(itemMapper.toItemDto(existingItem)).thenReturn(expectedDto);

        ResponseEntity<ItemResponseDto> response = itemService.updateItem(itemId, changeItemDto, userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(
                        ItemResponseDto::getId,
                        ItemResponseDto::getName,
                        ItemResponseDto::getDescription,
                        ItemResponseDto::getAvailable
                )
                .containsExactly(
                        itemId,
                        "Item",
                        "Description",
                        true
                );

        verify(userRepository).findUserById(userId);
        verify(itemRepository).findByIdAndOwnerId(itemId, userId);
        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(argThat(item ->
                item.getName().equals("Item") &&
                        item.getDescription().equals("Description")
        ));
        verify(itemMapper).toItemDto(existingItem);
    }

    @Test
    void testUpdateItem_whenUserNotOwner_thenThrowAccessDenied() {
        when(userRepository.findUserById(otherUser)).thenReturn(Optional.of(notOwner));
        when(itemRepository.findByIdAndOwnerId(ID, otherUser)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                itemService.updateItem(ID, changeItemDto, otherUser)
        );
    }

    @Test
    void updateItem_whenItemNotFound_shouldThrowNotFoundExceptionAndLogError() {
        when(userRepository.findUserById(ID)).thenReturn(Optional.of(new User()));

        when(itemRepository.findByIdAndOwnerId(ID, ID)).thenReturn(Optional.of(new Item()));

        when(itemRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(ID, new ChangeItemDto(), ID));

        verify(itemRepository, never()).save(any());

        Throwable thrown = catchThrowable(() ->
                itemService.updateItem(ID, new ChangeItemDto(), ID));
        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Вещь с id");
    }

    @Test
    void updateItem_whenUpdatingAvailability_shouldUpdateAndLogChange() {
        boolean newAvailability = !item.getAvailable();
        ChangeItemDto updateDto = new ChangeItemDto();
        updateDto.setAvailable(newAvailability);

        when(userRepository.findUserById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(ID, ID)).thenReturn(Optional.of(item));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<ItemResponseDto> response = itemService.updateItem(ID, updateDto, ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());

        Item savedItem = itemCaptor.getValue();
        assertThat(savedItem.getAvailable()).isEqualTo(newAvailability);
        assertThat(savedItem.getName()).isEqualTo(item.getName());
        assertThat(savedItem.getDescription()).isEqualTo(item.getDescription());
    }

    @Test
    void updateItem_whenAvailabilityNotChanged_shouldNotUpdate() {
        ChangeItemDto updateDto = new ChangeItemDto();
        updateDto.setAvailable(item.getAvailable());

        when(userRepository.findUserById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdAndOwnerId(ID, ID)).thenReturn(Optional.of(item));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<ItemResponseDto> response = itemService.updateItem(ID, updateDto, ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());

        Item savedItem = itemCaptor.getValue();
        assertThat(savedItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testGetItemById_whenItemFound_thenReturnedItem() {
        Long itemId = ID;
        Item item = new Item(itemId, "Item", "Description", true, user, null);
        ItemWithBookingsDto expectedDto = new ItemWithBookingsDto(itemId, "Item", "Description", true, "User");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toDtoWithBookingsAndComments(item, Set.of())).thenReturn(expectedDto);

        ResponseEntity<ItemWithBookingsDto> response = itemService.getItemById(itemId);
        ItemWithBookingsDto actualItemDto = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualItemDto)
                .isNotNull()
                .extracting(
                        ItemWithBookingsDto::getId,
                        ItemWithBookingsDto::getName,
                        ItemWithBookingsDto::getDescription
                )
                .containsExactly(
                        itemId,
                        "Item",
                        "Description"
                );

        verify(itemRepository).findById(itemId);
        verify(itemMapper).toDtoWithBookingsAndComments(item, Set.of());
    }

    @Test
    void testGetItemById_whenItemNotFound_thenExceptionThrown() {
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(2L));
    }

    @Test
    void testGetItemsByOwner_CorrectArgumentsForPaging_thenReturnItems() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        var response = itemService.getItemsByOwner(ID);
        List<ItemWithBookingsDto> targetItems = response.getBody();

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository).findAllByOwnerId(anyLong());
    }

    @Test
    void testSaveNewComment_whenUserWasBooker_thenSavedComment() {
        Long userId = ID;
        Long itemId = ID;
        User user = new User(userId, "User", "user@yandex.ru");
        Item item = new Item(itemId, "Item", "Description", true, user, null);
        ChangeCommentDto requestDto = new ChangeCommentDto("Test comment");
        Comment savedComment = new Comment(ID, "Test comment", item, Instant.now(), user);
        CommentResponseDto expectedDto = new CommentResponseDto(ID, "Test comment", Instant.now(), "User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                eq(userId),
                eq(itemId),
                any(LocalDateTime.class))
        ).thenReturn(true);
        when(commentMapper.toEntity(requestDto, user, item)).thenReturn(savedComment);
        when(commentRepository.save(savedComment)).thenReturn(savedComment);
        when(commentMapper.toCommentDto(savedComment)).thenReturn(expectedDto);

        ResponseEntity<CommentResponseDto> response = itemService.addComment(itemId, requestDto, userId);
        CommentResponseDto actualComment = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actualComment)
                .isNotNull()
                .extracting(
                        CommentResponseDto::getText,
                        CommentResponseDto::getAuthorName
                )
                .containsExactly(
                        "Test comment",
                        "User"
                );

        verify(commentRepository).save(savedComment);
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndBefore(
                eq(userId),
                eq(itemId),
                any(LocalDateTime.class));
    }

    @Test
    void testSaveNewComment_whenUserWasNotBooker_thenThrownException() throws Exception {
        Long userId = ID;
        Long itemId = 2L;
        User user = new User(userId, "User", "user@mail.ru");
        User owner = new User(2L, "Owner", "owner@mail.ru");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                eq(userId),
                eq(itemId),
                any(LocalDateTime.class))
        ).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> itemService.addComment(itemId, new ChangeCommentDto("Text"), userId));

        verify(userRepository).findById(userId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).existsByBookerIdAndItemIdAndEndBefore(
                eq(userId),
                eq(itemId),
                any(LocalDateTime.class));
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toEntity(any(), any(), any());
    }

    @Test
    void testSearchItem_whenTextIsBlank_shouldReturnEmptyList() {
        ResponseEntity<List<ItemResponseDto>> response = itemService.searchItem(" ");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(itemRepository, never()).findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                anyString(), anyString());
    }

    @Test
    void testSearchItem_whenTextIsNull_shouldReturnEmptyList() {
        ResponseEntity<List<ItemResponseDto>> response = itemService.searchItem(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(itemRepository, never()).findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                anyString(), anyString());
    }

    @Test
    void testSearchItem_whenNoItemsFound_shouldReturnEmptyList() {
        when(itemRepository.findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                searchText, searchText)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ItemResponseDto>> response = itemService.searchItem(searchText);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void searchItem_shouldCallMapperWithCorrectItems() {
        List<ItemResponseDto> expectedDto = List.of(
                new ItemResponseDto(1L, "Item1", "Desc1", true, null, null),
                new ItemResponseDto(2L, "Item2", "Desc2", true, null, null)
        );

        when(itemRepository.findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                searchText, searchText))
                .thenReturn(testItems);

        when(itemMapper.toItemDtoList(testItems)).thenReturn(expectedDto);

        ResponseEntity<List<ItemResponseDto>> response = itemService.searchItem(searchText);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isEqualTo(expectedDto)
                .hasSize(2);

        verify(itemMapper).toItemDtoList(testItems);
        verify(itemRepository).findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                searchText, searchText);
    }

    @Test
    void deleteItemById_whenItemExists_shouldDeleteItem() {
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).delete(item);

        ResponseEntity<Void> response = itemService.deleteItemById(ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(itemRepository).delete(item);
    }

    @Test
    void deleteItemById_whenItemNotExists_shouldThrowNotFoundException() {
        when(itemRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(ID));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void deleteItemById_whenRepositoryThrowsException_shouldPropagateException() {
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));
        doThrow(new DataAccessException("Database error") {
        }).when(itemRepository).delete(item);

        assertThrows(DataAccessException.class, () -> itemService.deleteItemById(ID));
    }
}