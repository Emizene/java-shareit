package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ResponseEntity<ItemRequestResponseDto> addRequest(Long userId, ItemRequestDto request) {
        log.debug("Создание нового запроса с ID{}: ", request.getId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest entity = requestMapper.toEntity(request);
        entity.setRequestor(user);
        requestRepository.save(entity);
        log.info("Успешное создание запроса с ID{}", request.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(requestMapper.toDto(entity));
    }

    @Override
    public ResponseEntity<List<ItemRequestResponseDto>> getRequestsByUser(Long userId) {
        log.debug("Запросы пользователя с ID{}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return ResponseEntity.ok(requestMapper.toItemRequestDtoList(requests));
    }

    @Override
    public ResponseEntity<List<ItemRequestResponseDto>> getAllAvailableRequests(Integer from, Integer size, Long userId) {
        log.debug("Доступные запросы для  пользователя с ID{}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdIsNot(userId, pageable);

        return ResponseEntity.ok(requestMapper.toItemRequestDtoList(requests));
    }

    @Override
    public ResponseEntity<ItemRequestResponseDto> getRequestById(Long requestId) {
        log.info("Поиск запроса с ID {}", requestId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        log.info("Найден запрос: ID{}", requestId);

        List<Item> item = itemRepository.findByRequestId(requestId);

        return ResponseEntity.ok(requestMapper.toDtoWithItems(request, item));
    }
}
