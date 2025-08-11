package ru.practicum.shareit.request.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface RequestService {
    ResponseEntity<ItemRequestResponseDto> addRequest(Long userId, ItemRequestDto request);

    ResponseEntity<List<ItemRequestResponseDto>> getRequestsByUser(Long userId);

    ResponseEntity<List<ItemRequestResponseDto>> getAllAvailableRequests(Integer from, Integer size, Long userId);

    ResponseEntity<ItemRequestResponseDto> getRequestById(Long requestId);
}
