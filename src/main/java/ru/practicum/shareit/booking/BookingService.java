package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;

import java.util.List;

public interface BookingService {

    ResponseEntity<BookingResponseDto> createBooking(ChangeBookingDto booking, Long userId);

    ResponseEntity<BookingResponseDto> updateBooking(ChangeBookingDto booking, Long bookingId, Long userId, Boolean approved);

    ResponseEntity<BookingResponseDto> getBookingById(Long bookingId, Long userId);

    ResponseEntity<Void> deleteBookingById(Long bookingId);

    ResponseEntity<List<BookingResponseDto>> getAllUserBookings(Long userId, String status);

    ResponseEntity<List<BookingResponseDto>> getAllOwnerBookings(Long userId, String status);
}
