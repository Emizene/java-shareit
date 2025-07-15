package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.ChangeCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ChangeItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ResponseEntity<ItemResponseDto> createItem(ChangeItemDto item, Long userId) {
        log.debug("Добавление новой вещи: item={}", item.getName());

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Item entity = itemMapper.toEntity(item);
        entity.setOwner(owner);
        itemRepository.save(entity);
        log.info("Успешное добавление вещи: ID={}, владелец ID={}", entity.getId(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toItemDto(entity));
    }

    @Override
    @Transactional
    public ResponseEntity<ItemResponseDto> updateItem(Long itemId, ChangeItemDto item, Long userId) {
        log.debug("Обновление существующей вещи с ID {}", item.getId());

        userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new AccessDeniedException("Пользователь не является владельцем: доступ запрещен"));

        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с ID {} не найдена для обновления", itemId);
                    return new NotFoundException("Вещь с id " + item.getId() + " не найдена");
                });

        if (item.getName() != null && !updatedItem.getName().equals(item.getName())) {
            log.debug("Обновление имени вещи с {} на {}", updatedItem.getName(), item.getName());
            updatedItem.setName(item.getName());
        }

        if (item.getDescription() != null && !updatedItem.getDescription().equals(item.getDescription())) {
            log.debug("Обновление описания вещи с {} на {}", updatedItem.getDescription(), item.getDescription());
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null && !updatedItem.getAvailable().equals(item.getAvailable())) {
            log.debug("Обновление доступности вещи с {} на {}", updatedItem.getAvailable(), item.getAvailable());
            updatedItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(updatedItem);
        log.info("Вещь с ID {} успешно обновлена", item.getId());

        return ResponseEntity.ok().body(itemMapper.toItemDto(updatedItem));
    }

    @Override
    public ResponseEntity<ItemResponseDto> getItemById(Long itemId) {
        log.debug("Запрос вещи с ID {}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID %s не найдена".formatted(itemId)));

        log.info("Найдена вещь: ID={}", itemId);

        return ResponseEntity.ok(itemMapper.toItemDto(item));
    }

    @Override
    public ResponseEntity<List<ItemDtoWithBookings>> getItemsByOwner(Long ownerId) {
        log.debug("Запрос всех вещей пользователя с ID {}", ownerId);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(ownerId)));

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        List<ItemDtoWithBookings> result = items.stream()
                .map(itemMapper::toDtoWithBookings)
                .collect(Collectors.toList());

        log.info("Найдено {} вещей для пользователя ID={}", result.size(), ownerId);
        return ResponseEntity.ok(result);
    }


    @Override
    public ResponseEntity<List<ItemResponseDto>> searchItem(String searchText) {
        log.debug("Запрос вещи с названием: searchText={}", searchText);

        if (searchText == null || searchText.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Item> items = itemRepository.findByNameContainingIgnoreCaseAndAvailableIsTrueOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(searchText, searchText);

        if (items.isEmpty()) {
            log.debug("Вещей по запросу searchText={} не найдено", searchText);
            return ResponseEntity.ok(Collections.emptyList());
        }

        log.info("Возвращено {} вещей", items.size());
        return ResponseEntity.ok(itemMapper.toItemDtoList(items));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID %s не найдена".formatted(itemId)));

        itemRepository.delete(item);

        log.info("Вещь с ID: ID={} удалена", itemId);

        return ResponseEntity.ok().build();
    }

//    @Override
//    public ResponseEntity<List<ItemResponseDto>> getAllUserItems(Long userId) {
//        log.debug("Запрос всех вещей пользователя с ID={}", userId);
//
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
//
//        List<Item> items = itemRepository.findAll().stream()
//                .filter(e -> Objects.equals(e.getOwner().getId(), owner.getId()))
//                .toList();
//
//        log.info("Возвращено {} вещей", items.size());
//        return ResponseEntity.ok(itemMapper.toItemDtoList(items));
//    }

//    @Override
//    public ResponseEntity<List<ItemDtoWithBookings>> getAllUserItems(Long userId) {
//        log.debug("Запрос всех вещей пользователя с ID={}", userId);
//
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
//
//        List<Item> items = itemRepository.findAll().stream()
//                .filter(e -> Objects.equals(e.getOwner().getId(), owner.getId()))
//                .toList();
//
//        log.info("Возвращено {} вещей", items.size());
//        return ResponseEntity.ok(itemMapper.toDtoWithBookings(items));
//    }

    @Override
    public ResponseEntity<CommentResponseDto> addComment(Long itemId, ChangeCommentDto comment, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

//        if (!bookingRepository.hasUserBookedItem(itemId, userId, LocalDateTime.now())) {
//            throw new AccessDeniedException("Нельзя оставить отзыв: вы не брали эту вещь в аренду или аренда не завершена");
//        }

//        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdOrderByStartAsc(userId, itemId)
//                .orElseThrow(() -> new AccessDeniedException("Нельзя оставить отзыв: вы не брали эту вещь в аренду или аренда не завершена"));
//
//        if (booking.getEnd().isAfter(LocalDateTime.now())) {
//            throw new IllegalStateException("Срок аренды ещё не истек");
//        }

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new AccessDeniedException("Нельзя оставить отзыв: вы не брали эту вещь в аренду");
        }

        Comment entity = commentMapper.toEntity(comment, author, item);
//        entity.setItem(item);
//        entity.setAuthor(author);
//        entity.setText(comment.getText());
        commentRepository.save(entity);
        log.info("Успешное добавление комментария пользователя с ID={} к вещи с ID={}", userId, itemId);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toCommentDto(entity));
    }
}
