
package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.SharedHeaders;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;


@Controller
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> manageBooking(
            @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        log.info("Managing booking {} for user {}, approved: {}", bookingId, userId, approved);
        return bookingClient.manageBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerId(
            @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting bookings for owner {}, state {}, from {}, size {}", userId, state, from, size);
        return bookingClient.findByOwnerId(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}