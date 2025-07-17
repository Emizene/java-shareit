package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ChangeBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ResponseEntity<BookingResponseDto> createBooking(ChangeBookingDto booking, Long userId) {
        log.debug("Добавление нового запроса на бронирование с ID {}", booking.getItemId());

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + booking.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new InternalServerErrorException("Вещь недоступна для бронирования");
        }

        if (booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Срок аренды указан некорректно");
        }

        Booking entity = bookingMapper.toEntity(booking);
        entity.setStatus(Status.WAITING);
        entity.setBooker(user);
        bookingRepository.save(entity);
        log.info("Успешное добавление запроса: ID={}, пользователь с запросом ID={}", entity.getId(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toBookingDto(entity));
    }

    @Override
    @Transactional
    public ResponseEntity<BookingResponseDto> updateBooking(Long bookingId, Long userId, Boolean approved) {
        log.debug("Обновление статуса запроса с ID {}", bookingId);

        Booking updatedBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID " + bookingId + " не найдена"));

        if (!updatedBooking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Подтвердить бронирование может только владелец вещи");
        }

        if (approved) {
            log.debug("Обновление статуса на {}", updatedBooking.getStatus());
            updatedBooking.setStatus(Status.APPROVED);
        } else {
            log.debug("Обновление доступности вещи на {}", updatedBooking.getStatus());
            updatedBooking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(updatedBooking);
        log.info("Запрос с ID {} успешно обновлён", bookingId);

        return ResponseEntity.ok().body(bookingMapper.toBookingDto(updatedBooking));
    }

    @Override
    public ResponseEntity<BookingResponseDto> getBookingById(Long bookingId, Long userId) {
        log.debug("Запрос бронирования с ID {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID %s не найдена".formatted(bookingId)));

        boolean isAuthor = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isAuthor && !isOwner) {
            throw new AccessDeniedException("Доступ запрещен: пользователь не является ни автором бронирования, ни владельцем вещи");
        }

        log.info("Найдена бронь: ID={}", bookingId);

        return ResponseEntity.ok(bookingMapper.toBookingDto(booking));
    }

    @Override
    public ResponseEntity<Void> deleteBookingById(Long bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID %s не найдена".formatted(bookingId)));

        bookingRepository.deleteById(bookingId);

        log.info("Бронь с ID={} удалена", bookingId);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<BookingResponseDto>> getAllUserBookings(Long userId, String state) {
        log.debug("Получение списка бронирований пользователя с ID {} со статусом {}", userId, state);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST" -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                    userId, LocalDateTime.now());
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                    userId, LocalDateTime.now());
            case "WAITING", "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, Status.valueOf(state.toUpperCase()));
            default -> throw new InternalServerErrorException("Был введен некорректный  статус" + state);
        };

        return ResponseEntity.ok(bookingMapper.toBookingDtoList(bookings));
    }

    @Override
    public ResponseEntity<List<BookingResponseDto>> getAllOwnerBookings(Long userId, String state) {
        log.debug("Получение списка бронирований владельца ID {} со статусом {}", userId, state);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!itemRepository.existsByOwnerId(userId)) {
            throw new AccessDeniedException("Пользователь не является владельцем вещей");
        }

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(
                    userId, LocalDateTime.now());
            case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(
                    userId, LocalDateTime.now());
            case "WAITING", "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    userId, Status.valueOf(state.toUpperCase()));
            default -> throw new InternalServerErrorException("Был введен некорректный статус" + state);
        };

        return ResponseEntity.ok(bookingMapper.toBookingDtoList(bookings));
    }
}
