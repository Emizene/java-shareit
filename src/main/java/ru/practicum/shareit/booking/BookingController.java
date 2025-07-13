package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;

import java.util.List;

@RequiredArgsConstructor
@Valid
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody ChangeBookingDto booking,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(@Valid @RequestBody ChangeBookingDto booking,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @PathVariable Long bookingId,
                                                            @RequestParam(name = "approved") Boolean approved) {
        return bookingService.updateBooking(booking, bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                       @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllOwnerBookings(userId, state);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBookingById(@PathVariable Long bookingId) {
        return bookingService.deleteBookingById(bookingId);
    }
}
