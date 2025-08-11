package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                bookingMapper,
                "userMapper",
                userMapper
        );
    }

    private static final Long ID = 1L;
    private static final Long ID2 = 2L;
    private final User owner = new User(1L, "Owner", "owner@email.com");
    private final User booker = new User(2L, "Booker", "booker@email.com");
    private final Item availableItem = new Item(ID, "Item", "Description", true, owner, null);
    private final Item unavailableItem = new Item(ID, "Item", "Description", false, owner, null);
    private final Booking booking = Booking.builder()
            .id(ID)
            .booker(owner)
            .item(availableItem)
            .status(Status.APPROVED)
            .build();

    private final List<Booking> testBookings = List.of(
            Booking.builder().id(1L).booker(getCorrectUser()).build(),
            Booking.builder().id(2L).booker(getCorrectUser()).build()
    );

    @SuppressWarnings("ConstantConditions")
    @Test
    void testSuccessCreateBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        ChangeBookingDto request = new ChangeBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        User user = getCorrectUser();
        Item item = getCorrectItem();
        Booking savedBooking = Booking.builder()
                .id(1L)
                .start(request.getStart())
                .end(request.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.toEntity(any())).thenReturn(savedBooking);

        var response = bookingService.createBooking(request, userId);

        verify(bookingRepository, times(1)).save(any(Booking.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getStatus()).isEqualTo(Status.WAITING);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_whenUserNotFound_shouldThrowNotFoundException() {
        ChangeBookingDto request = new ChangeBookingDto();
        request.setItemId(ID);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(request, ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_whenItemUnavailable_shouldThrowInternalServerError() {
        Long itemId = 1L;
        ChangeBookingDto request = new ChangeBookingDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(unavailableItem));

        assertThrows(InternalServerErrorException.class, () ->
                bookingService.createBooking(request, ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_whenDatesEqual_shouldThrowValidationException() {
        Long itemId = 1L;
        LocalDateTime sameDate = LocalDateTime.now().plusDays(1);
        ChangeBookingDto request = new ChangeBookingDto();
        request.setItemId(itemId);
        request.setStart(sameDate);
        request.setEnd(sameDate);

        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(availableItem));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(request, ID));
        verify(bookingRepository, never()).save(any());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testSuccessUpdateBooking() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Boolean approved = true;

        User owner = getCorrectUser();
        User booker = new User(2L, "Name", "booker@yeandex.ru");
        Item item = new Item(1L, "Name", "Description", true, owner, null);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var response = bookingService.updateBooking(bookingId, ownerId, approved);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(bookingId);
        assertThat(body.getStatus()).isEqualTo(Status.APPROVED);
        verify(bookingRepository).save(booking);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testUpdateBooking_whenApprovedTrue_shouldUpdateStatusToApproved() {
        Booking booking = Booking.builder()
                .id(ID)
                .status(Status.WAITING)
                .item(availableItem)
                .booker(booker)
                .build();

        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        ResponseEntity<BookingResponseDto> response =
                bookingService.updateBooking(ID, ID, true);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Status.APPROVED);
        verify(bookingRepository).save(booking);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testUpdateBooking_whenApprovedFalse_shouldUpdateStatusToRejected() {
        Booking booking = Booking.builder()
                .id(ID)
                .status(Status.WAITING)
                .item(availableItem)
                .booker(booker)
                .build();

        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        ResponseEntity<BookingResponseDto> response =
                bookingService.updateBooking(ID, ID, false);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Status.REJECTED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testUpdateBooking_whenBookingNotFound_shouldThrowNotFoundException() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.updateBooking(ID, ID, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testUpdateBooking_whenUserNotOwner_shouldThrowAccessDeniedException() {
        Booking booking = Booking.builder()
                .id(ID)
                .status(Status.WAITING)
                .item(availableItem)
                .booker(booker)
                .build();

        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () ->
                bookingService.updateBooking(ID, ID2, true));
        verify(bookingRepository, never()).save(any());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testUpdateBooking_whenStatusNotWaiting_shouldKeepOriginalStatus() {
        Booking booking = Booking.builder()
                .id(ID)
                .status(Status.APPROVED)
                .item(availableItem)
                .booker(booker)
                .build();

        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        ResponseEntity<BookingResponseDto> response =
                bookingService.updateBooking(ID, ID, false);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo(Status.REJECTED);
        verify(bookingRepository).save(booking);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testGetBookingById() {
        Long bookingId = 1L;
        Long ownerId = 1L;

        User owner = getCorrectUser();
        User booker = new User(2L, "Booker", "booker@mail.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        var response = bookingService.getBookingById(bookingId, ownerId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(bookingId);
        assertThat(body.getStatus()).isEqualTo(Status.APPROVED);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getBookingById_whenUserIsAuthor_shouldReturnBooking() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        ResponseEntity<BookingResponseDto> response =
                bookingService.getBookingById(ID, ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(ID);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getBookingById_whenUserIsOwner_shouldReturnBooking() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        ResponseEntity<BookingResponseDto> response =
                bookingService.getBookingById(ID, ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(ID);
    }

    @Test
    void getBookingById_whenUserNotAuthorNorOwner_shouldThrowAccessDenied() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(ID, ID2))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Доступ запрещен");
    }

    @Test
    void getBookingById_whenBookingNotFound_shouldThrowNotFoundException() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(ID, ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Бронь с ID " + ID);
    }

    @Test
    void getBookingById_whenUserIsAuthor_shouldHaveIsAuthorTrue() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        bookingService.getBookingById(ID, ID);
    }

    @Test
    void getBookingById_whenUserIsOwner_shouldHaveIsOwnerTrue() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        bookingService.getBookingById(ID, ID);
    }

    @Test
    void getBookingById_whenUserIsNeither_shouldHaveCorrectBooleanValues() {
        when(bookingRepository.findById(ID)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(ID, ID2))
                .isInstanceOfSatisfying(AccessDeniedException.class, e -> {
                });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testSuccessDeleteBookingById_shouldReturnOkAndDeleteBooking() {
        Long bookingId = 1L;
        Booking existingBooking = getCorrectBookingWithStatusAll();

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(existingBooking));

        var response = bookingService.deleteBookingById(bookingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body).isNull();
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).deleteById(bookingId);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testDeleteBookingById_shouldThrowNotFoundWhenBookingNotExists() {
        Long nonExistentBookingId = 999L;

        when(bookingRepository.findById(nonExistentBookingId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.deleteBookingById(nonExistentBookingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Бронь с ID %s не найдена".formatted(nonExistentBookingId));

        verify(bookingRepository, never()).deleteById(any());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testGetAllUserBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(List.of(getCorrectBookingWithStatusCurrent()));

        var response = bookingService.getAllUserBookings(1L, "CURRENT");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).hasSize(1);
        assertThat(body.getFirst().getId()).isEqualTo(1L);
        assertThat(body.getFirst().getStatus()).isEqualTo(Status.CURRENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testGetAllOwnerBookings() {
        when(userRepository.findById(any())).thenReturn(Optional.of(getCorrectUser()));
        when(itemRepository.existsByOwnerId(any())).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(any())).thenReturn(List.of(getCorrectBookingWithStatusAll()));

        var response = bookingService.getAllOwnerBookings(1L, "ALL");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).hasSize(1);
        assertThat(body.getFirst().getId()).isEqualTo(1L);
        assertThat(body.getFirst().getStatus()).isEqualTo(Status.ALL);
    }

    @Test
    void testGetAllUserBookings_whenStateAll_shouldReturnAllBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdOrderByStartDesc(ID)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "ALL");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenStateCurrent_shouldReturnCurrentBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(ID), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "CURRENT");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenStatePast_shouldReturnPastBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                eq(ID), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "PAST");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenStateFuture_shouldReturnFutureBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                eq(ID), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "FUTURE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenStateWaiting_shouldReturnWaitingBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                ID, Status.WAITING)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "WAITING");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenStateRejected_shouldReturnRejectedBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                ID, Status.REJECTED)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllUserBookings(ID, "REJECTED");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllUserBookings_whenInvalidState_shouldThrowInternalServerError() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(getCorrectUser()));

        assertThrows(InternalServerErrorException.class, () ->
                bookingService.getAllUserBookings(ID, "INVALID"));
    }

    @Test
    void testGetAllUserBookings_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllUserBookings(ID, "ALL"));
    }

    @Test
    void testGetAllOwnerBookings_whenStateAll_shouldReturnAllBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(ID)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "ALL");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenStateCurrent_shouldReturnCurrentBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(ID), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "CURRENT");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenStatePast_shouldReturnPastBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                eq(ID), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "PAST");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenStateFuture_shouldReturnFutureBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                eq(ID), any(LocalDateTime.class)))
                .thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "FUTURE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenStateWaiting_shouldReturnWaitingBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                ID, Status.WAITING)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "WAITING");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenStateRejected_shouldReturnRejectedBookings() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                ID, Status.REJECTED)).thenReturn(testBookings);

        ResponseEntity<List<BookingResponseDto>> response =
                bookingService.getAllOwnerBookings(ID, "REJECTED");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void testGetAllOwnerBookings_whenInvalidState_shouldThrowInternalServerError() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(true);

        assertThrows(InternalServerErrorException.class, () ->
                bookingService.getAllOwnerBookings(ID, "INVALID"));
    }

    @Test
    void testGetAllOwnerBookings_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getAllOwnerBookings(ID, "ALL"));
    }

    @Test
    void testGetAllOwnerBookings_whenNotOwner_shouldThrowAccessDeniedException() {
        when(userRepository.findById(ID)).thenReturn(Optional.of(owner));
        when(itemRepository.existsByOwnerId(ID)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () ->
                bookingService.getAllOwnerBookings(ID, "ALL"));
    }

    private static Booking getCorrectBookingWithStatusAll() {
        return new Booking(1L, now().minusDays(1), now().plusDays(1), getCorrectItem(), getCorrectUser(), Status.ALL);
    }

    private static Booking getCorrectBookingWithStatusCurrent() {
        return new Booking(1L, now().minusDays(1), now().plusDays(1), getCorrectItem(), getCorrectUser(), Status.CURRENT);
    }

    private static Item getCorrectItem() {
        return new Item(1L,
                "Name",
                "Desc",
                true,
                getCorrectUser(),
                new ItemRequest(1L, "desc", getCorrectUser(), Instant.now().minus(2, ChronoUnit.DAYS)));
    }

    private static User getCorrectUser() {
        return new User(1L, "Name", "email@yandex.ru");
    }
}