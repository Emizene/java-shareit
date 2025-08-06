package item;

import jakarta.persistence.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private final User user = new User(ID, "User", "user@yandex.ru");
    private final User notOwner = new User(2L, "User2", "user2@yandex.ru");
    private final ChangeItemDto changeItemDto = new ChangeItemDto(ID, "Item", "Description", true, null);
    private final ItemDtoWithBookings itemDtoOut = new ItemDtoWithBookings(ID, "Item", "Description", true, "User");
    private final Item item = new Item(ID, "Item", "Description", true, user, null);
    private final CommentResponseDto commentDto = new CommentResponseDto(ID, "Text", Instant.now(), "User");
    private final Comment comment = new Comment(ID, "Text", item, Instant.now(), user);
    private final Booking booking = new Booking(ID, null, null, item, user, Status.WAITING);

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

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(changeItemDto, 2L));
    }

//    @Test
//    void testSuccessUpdateItem_whenUserIsOwner_thenUpdatedItem() {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
//        when(itemRepository.save(any())).thenReturn(item);
//        when(itemMapper.toItemDto(any())).thenReturn(any());
//
//        var actualItemDto = itemService.updateItem(ID, changeItemDto, ID);
//
//        Assertions.assertNotNull(actualItemDto);
//        verify(itemMapper).toItemDto(any());
//    }

//    @Test
//    void testUpdateItem_whenUserNotOwner_thenNotUpdatedItem() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(notOwner));
//        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
//
//        Assertions.assertThrows(AccessDeniedException.class,
//                () -> itemService.updateItem(id, changeItemDto, 2L));
//    }

//    @Test
//    void testGetItemById_whenItemFound_thenReturnedItem() {
//        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(
//                anyLong(), any(), any(), any()))
//                .thenReturn(Optional.of(booking));
//        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatus(
//                anyLong(), any(), any(), any()))
//                .thenReturn(Optional.of(booking));
//        when(commentRepository.findAllByItemId(id)).thenReturn(List.of(comment));
//        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
//
//        var actualItemDto = itemService.getItemById(id);
//
//        Assertions.assertNotNull(actualItemDto);
//        verify(itemMapper).toDtoWithBookings(item);
//        verify(bookingMapper, times(2)).toBookingDtoSimple(booking);
//        verify(commentMapper).toCommentDto(comment);
//    }

    @Test
    void testGetItemById_whenItemNotFound_thenExceptionThrown() {
        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(2L));
    }

    @Test
    void testGetItemsByOwner_CorrectArgumentsForPaging_thenReturnItems() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        var response = itemService.getItemsByOwner(ID);
        List<ItemDtoWithBookings> targetItems = response.getBody();

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository).findAllByOwnerId(anyLong());
    }

//    @Test
//    void testSaveNewComment_whenUserWasBooker_thenSavedComment() {
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
//        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
//                anyLong(), anyLong(), any()))
//                .thenReturn(true);
//        when(commentRepository.save(any())).thenReturn(commentDto);
//        when(itemMapper.toItemDto(any())).thenReturn(any());
//
//        var actualComment = itemService.addComment(ID, getComment(), ID);
//
//        Assertions.assertEquals(commentDto.getText(), actualComment.getText());
//        verify(commentMapper).toCommentDto(comment);
//    }

    @Test
    void testSaveNewComment_whenUserWasNotBooker_thenThrownException() throws Exception {
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                anyLong(), anyLong(), any()))
                .thenReturn(false);

        Assertions.assertThrows(AccessDeniedException.class,
                () -> itemService.addComment(2L, new ChangeCommentDto("Text"), ID));
    }

    private static ChangeCommentDto getComment() {
        return new ChangeCommentDto("Text");
    }
}