package item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    private final Long id = 1L;
    private final User user = new User(id, "User", "user@yandex.ru");
    private final User notOwner = new User(2L, "User2", "user2@yandex.ru");
    private final ChangeItemDto changeItemDto = new ChangeItemDto(id, "Item", "Description", true, null);
    private final ItemDtoWithBookings itemDtoOut = new ItemDtoWithBookings(id, "Item", "Description", true, "User");
    private final Item item = new Item(id, "Item", "Description", true, user, null);
    private final CommentResponseDto commentDto = new CommentResponseDto(id, "Text", Instant.now(), "User");
    private final Comment comment = new Comment(id, "Text", item, Instant.now(), user);
    private final Booking booking = new Booking(id, null, null, item, user, Status.WAITING);

//    @Test
//    void testSuccessCreateItem_whenUserFound_thenSavedItem() {
//        ChangeItemDto requestDto = new ChangeItemDto(null, "Item", "Description", true, null);
//        Item newItem = new Item(null, "Item", "Description", true, user, null);
//        Item savedItem = new Item(id, "Item", "Description", true, user, null);
//
//        // Настройка моков
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//        when(itemMapper.toItem(requestDto)).thenReturn(newItem);
//        when(itemRepository.save(newItem)).thenReturn(savedItem);
//        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDtoOut);
//
//        // Выполнение
//        var actualItemDto = itemService.createItem(requestDto, id);
//
//        // Проверки
//        verify(userRepository).findById(id);
//        verify(itemMapper).toItem(requestDto);
//        verify(itemRepository).save(newItem);
//        verify(itemMapper).toItemDto(savedItem);
//
//        Assertions.assertNotNull(actualItemDto);
//        Assertions.assertEquals(itemDtoOut, actualItemDto);
//    }

    @Test
    void testCreateItem_whenUserNotFound_thenNotSavedItem() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createItem(changeItemDto, 2L));
    }

//    @Test
//    void testSuccessUpdateItem_whenUserIsOwner_thenUpdatedItem() {
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
//        when(itemRepository.save(any())).thenReturn(item);
//
//        var actualItemDto = itemService.updateItem(id, changeItemDto, id);
//
//        Assertions.assertNotNull(actualItemDto);
//        verify(itemMapper).toDtoWithBookings(item);
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
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        var response = itemService.getItemsByOwner(id);
        List<ItemDtoWithBookings> targetItems = response.getBody();

        Assertions.assertNotNull(targetItems);
        Assertions.assertEquals(1, targetItems.size());
        verify(itemRepository).findAllByOwnerId(anyLong());
    }

//    @Test
//    void testSaveNewComment_whenUserWasBooker_thenSavedComment() {
//        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
//                anyLong(), anyLong(), any()))
//                .thenReturn(true);
//        when(userRepository.findById(id)).thenReturn(Optional.of(user));
//        when(commentRepository.save(any())).thenReturn(comment);
//        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
//
//        var actualComment = itemService.addComment(id, new ChangeCommentDto("abc"), id);
//
//        Assertions.assertEquals(commentDto.getText(), actualComment.getText());
//        verify(commentMapper).toCommentDto(comment);
//    }

//    @Test
//    void testSaveNewComment_whenUserWasNotBooker_thenThrownException() {
//        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
//                anyLong(), anyLong(), any()))
//                .thenReturn(false);
//
//        Assertions.assertThrows(AccessDeniedException.class,
//                () -> itemService.addComment(2L, new ChangeCommentDto("Text"), id));
//    }
}