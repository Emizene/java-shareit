package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.SharedHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Valid
@Slf4j
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> addRequest(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                                             @Valid @RequestBody ItemRequestDto request) {
        return requestService.addRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getRequestsByUser(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        return requestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllAvailableRequests(@RequestParam(defaultValue = "1") Integer from,
                                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                                @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        return requestService.getAllAvailableRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestsById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }
}
