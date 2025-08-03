package booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingTestDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void testSuccessCreateBooking() {
        Long userId = 1L;
        ChangeBookingDto request = new ChangeBookingDto();
        request.setItemId(1L);

        BookingResponseDto expectedBooking = new BookingResponseDto();
        expectedBooking.setId(1L);
        expectedBooking.setStart(LocalDateTime.now());
        expectedBooking.setEnd(LocalDateTime.now().plusDays(1));

        when(bookingService.createBooking(request, userId)).thenReturn(ResponseEntity.ok(expectedBooking));

        BookingResponseDto result = bookingController.createBooking(request, userId).getBody();

        Assertions.assertNotNull(result);
        assertEquals(expectedBooking.getId(), result.getId());
        verify(bookingService).createBooking(request, userId);
    }

    @Test
    void testSuccessApprovedAndUpdateBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingResponseDto expectedBooking = new BookingResponseDto();
        expectedBooking.setId(bookingId);
        expectedBooking.setStatus(Status.APPROVED);

        when(bookingService.updateBooking(userId, bookingId, approved)).thenReturn(ResponseEntity.ok(expectedBooking));

        BookingResponseDto result = bookingController.updateBooking(userId, bookingId, approved).getBody();

        assertEquals(expectedBooking, result);
        Assertions.assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingService).updateBooking(userId, bookingId, approved);
    }

    @Test
    void testSuccessRejectBooking() {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = false;

        BookingResponseDto expectedBooking = new BookingResponseDto();
        expectedBooking.setId(bookingId);
        expectedBooking.setStatus(Status.REJECTED);

        when(bookingService.updateBooking(userId, bookingId, approved)).thenReturn(ResponseEntity.ok(expectedBooking));

        BookingResponseDto result = bookingController.updateBooking(userId, bookingId, approved).getBody();

        assertEquals(expectedBooking, result);
        Assertions.assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
        verify(bookingService).updateBooking(userId, bookingId, approved);
    }

    @Test
    void testSuccessGetBookingById() {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingResponseDto expectedBooking = new BookingResponseDto();
        expectedBooking.setId(bookingId);

        when(bookingService.getBookingById(userId, bookingId)).thenReturn(ResponseEntity.ok(expectedBooking));

        BookingResponseDto result = bookingController.getBookingById(userId, bookingId).getBody();

        assertEquals(expectedBooking, result);
        verify(bookingService).getBookingById(userId, bookingId);
    }

    @Test
    void testSuccessGetAllUserBookings() {
        Long userId = 1L;
        Status state = Status.ALL;

        BookingResponseDto booking1 = new BookingResponseDto();
        booking1.setId(1L);
        BookingResponseDto booking2 = new BookingResponseDto();
        booking2.setId(2L);
        List<BookingResponseDto> expectedBookings = List.of(booking1, booking2);

        when(bookingService.getAllUserBookings(userId, String.valueOf(state))).thenReturn(ResponseEntity.ok(expectedBookings));

        List<BookingResponseDto> result = bookingController.getAllUserBookings(userId, String.valueOf(state)).getBody();

        Assertions.assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedBookings, result);
        verify(bookingService).getAllUserBookings(userId, String.valueOf(state));
    }

    @Test
    void testSuccessGetAllOwnerBookings() {
        Long userId = 1L;

        BookingResponseDto booking = new BookingResponseDto();
        booking.setId(1L);
        List<BookingResponseDto> expectedBookings = List.of(booking);

        when(bookingService.getAllOwnerBookings(userId, String.valueOf(Status.ALL))).thenReturn(ResponseEntity.ok(expectedBookings));

        List<BookingResponseDto> result = bookingController.getAllOwnerBookings(userId, String.valueOf(Status.ALL)).getBody();

        assertEquals(expectedBookings, result);
        verify(bookingService).getAllOwnerBookings(userId, String.valueOf(Status.ALL));
    }

    @Test
    void testSuccessDeleteBookingById() {
        Long bookingId = 1L;

        when(bookingService.deleteBookingById(bookingId))
                .thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = bookingController.deleteBookingById(bookingId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookingService).deleteBookingById(bookingId);
    }
}