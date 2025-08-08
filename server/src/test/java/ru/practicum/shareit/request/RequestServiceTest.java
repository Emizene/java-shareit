package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private RequestMapper requestMapper;
    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void testSuccessAddRequest() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Description");

        User user = new User(userId, "User", "user@yandex.ru");
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Description");
        request.setRequestor(user);

        ItemRequestResponseDto expectedDto = new ItemRequestResponseDto(
                1L, "Description", Instant.now(), userId, null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestMapper.toEntity(requestDto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(expectedDto);

        ResponseEntity<ItemRequestResponseDto> response = requestService.addRequest(userId, requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(
                        ItemRequestResponseDto::getId,
                        ItemRequestResponseDto::getDescription
                )
                .containsExactly(
                        1L,
                        "Description"
                );

        verify(requestRepository).save(request);
    }

    @Test
    void testSuccessGetRequestsByUser() {
        Long userId = 1L;
        User user = new User(userId, "User", "user@yandex.ru");
        ItemRequest request = new ItemRequest(1L, "Description", user, Instant.now());
        ItemRequestResponseDto expectedDto = new ItemRequestResponseDto(1L, "Description", Instant.now(), userId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request));
        when(requestMapper.toItemRequestDtoList(List.of(request))).thenReturn(List.of(expectedDto));

        ResponseEntity<List<ItemRequestResponseDto>> response =
                requestService.getRequestsByUser(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ItemRequestResponseDto> body = response.getBody();
        assertThat(body)
                .asList()
                .hasSize(1)
                .first()
                .extracting("description")
                .isEqualTo("Description");
    }

    @Test
    void testSuccessGetAllAvailableRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        User otherUser = new User(2L, "Other", "email@yandex.ru");
        ItemRequest request = new ItemRequest(1L, "Description", otherUser, Instant.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(requestRepository.findAllByRequestorIdIsNot(eq(userId), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(requestMapper.toItemRequestDtoList(List.of(request))).thenReturn(
                List.of(new ItemRequestResponseDto(1L, "Description", Instant.now(), 2L, null))
        );

        ResponseEntity<List<ItemRequestResponseDto>> response =
                requestService.getAllAvailableRequests(from, size, userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getFirst().getRequestorId()).isEqualTo(2L);
    }

    @Test
    void testSuccessGetRequestById() {
        Long requestId = 1L;
        Long userId = 1L;
        User user = new User(userId, "User", "ru.practicum.shareit.user@yandex.ru");
        ItemRequest request = new ItemRequest(requestId, "Description", user, Instant.now());
        Item item = new Item(1L, "Item", "Description", true, user, request);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));
        when(requestMapper.toDtoWithItems(request, List.of(item))).thenReturn(
                new ItemRequestResponseDto(requestId, "Description", Instant.now(), userId,
                        List.of(new ItemDtoSimple(1L, "Item")))
        );

        ResponseEntity<ItemRequestResponseDto> response =
                requestService.getRequestById(requestId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getItems().getFirst().getName()).isEqualTo("Item");
    }

    @Test
    void testSuccessGetRequestsByUser_whenUserNotFound_thenThrownException() {
        when((userRepository).findById(3L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                requestService.getRequestsByUser(3L));
    }
}