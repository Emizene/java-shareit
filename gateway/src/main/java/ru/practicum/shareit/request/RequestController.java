package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.SharedHeaders;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Valid
@RequestMapping("/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId,
                                                             @Valid @RequestBody ItemRequestDto request) {
        log.info("Post ru.practicum.shareit.request {}, userId={}", request, userId);
        return requestClient.addRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        log.info("Getting requests for ru.practicum.shareit.user={}", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAvailableRequests(@RequestParam(defaultValue = "1") Integer from,
                                                                                @RequestParam(defaultValue = "10") Integer size,
                                                                                @RequestHeader(SharedHeaders.USER_ID_HEADER) Long userId) {
        log.info("Getting available requests for ru.practicum.shareit.user={}", userId);
        return requestClient.getAllAvailableRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestsById(@PathVariable Long requestId) {
        log.info("Get ru.practicum.shareit.request={}", requestId);
        return requestClient.getRequestById(requestId);
    }
}
