package request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
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
    private final RequestMapper requestMapper = RequestMapper.builder().build();
    private final ItemMapper itemMapper = ItemMapper.builder().build();
    @InjectMocks
    private RequestServiceImpl requestService;
//    @Spy
//    RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);
//    @Spy
//    ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private final User requestor = new User(2L, "user2", "user2@mail.ru");
    private final User user = new User(1L, "User", "user@mail.ru");
    private final ItemRequest request = new ItemRequest(1L, "description", requestor, Instant.now());
    private final Item item = new Item(1L, "item", "cool", true, user, request);

    @Test
    void testSuccessAddRequest() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need item");

        User user = new User(userId, "User", "user@email.com");
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need item");
        request.setRequestor(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestMapper.toEntity(requestDto)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(
                new ItemRequestResponseDto(1L, "Need item", Instant.now(), userId, null)
        );

        ResponseEntity<ItemRequestResponseDto> response = requestService.addRequest(userId, requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDescription()).isEqualTo("Need item");
        verify(requestRepository).save(request);
    }

    @Test
    void testSuccessGetRequestsByUser() {
        Long userId = 1L;
        User user = new User(userId, "User", "user@email.com");
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

//    @Test
//    void testSuccessGetAllAvailableRequests() {
//        Long userId = 1L;
//        Integer from = 0;
//        Integer size = 10;
//        User otherUser = new User(2L, "Other", "email@yandex.ru");
//        ItemRequest request = new ItemRequest(1L, "Description", otherUser, Instant.now());
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
//        when(requestRepository.findAllByRequestorIdIsNot(eq(userId), any(Pageable.class)))
//                .thenReturn(List.of(request));
//        when(requestMapper.toItemRequestDtoList(List.of(request))).thenReturn(
//                List.of(new ItemRequestResponseDto(1L, "Description", Instant.now(), 2L, null))
//        );
//
//        ResponseEntity<List<ItemRequestResponseDto>> response =
//                requestService.getAllAvailableRequests(from, size, userId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).hasSize(1);
//        assertThat(response.getBody().get(0).getRequestorId()).isEqualTo(2L);
//    }

//    @Test
//    void testSuccessGetRequestById() {
//        Long requestId = 1L;
//        Long userId = 1L;
//        User user = new User(userId, "User", "user@yandex.ru");
//        ItemRequest request = new ItemRequest(requestId, "Description", user, Instant.now());
//        Item item = new Item(1L, "Item", "Description", true, user, request);
//
//        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
//        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));
//        when(requestMapper.toDtoWithItems(request, List.of(item))).thenReturn(
//                new ItemRequestResponseDto(requestId, "Description", Instant.now(), userId,
//                        List.of(new ItemDtoSimple(1L, "Item")))
//        );
//
//        ResponseEntity<ItemRequestResponseDto> response =
//                requestService.getRequestById(requestId);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().getItems()).hasSize(1);
//        assertThat(response.getBody().getItems().get(0).getName()).isEqualTo("Item");
//    }

    // =================================


//    @Test
//    void saveNewRequest() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
//        when(requestRepository.save(any())).thenReturn(request);
//
//        final ItemRequestResponseDto actualRequest = requestService.addRequest(new ItemRequestDto(2L, "description");
//
//        Assertions.assertEquals(requestMapper.toDto(request), actualRequest);
//    }

//    @Test
//    void getRequestsByRequestor_whenUserFound_thenSavedRequest() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(requestor));
//        when(requestRepository.findAllByRequestorId(anyLong(), any())).thenReturn(List.of(request));
//        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
//        final ItemRequestDto requestDtoOut = ItemRequestMapper.toItemRequestDto(request);
//        requestDtoOut.setItems(List.of(ItemMapper.toItemDto(item)));
//
//        List<ItemRequestDto> actualRequests = requestService.getRequestsByRequestor(2L);
//
//        Assertions.assertEquals(List.of(requestDtoOut), actualRequests);
//    }

//    @Test
//    void testGetRequestsByUser_whenUserNotFound_thenThrownException() {
//        when((userRepository).findById(3L)).thenReturn(Optional.empty());
//
//        Assertions.assertThrows(NotFoundException.class, () ->
//                requestService.getRequestsByUser(3L));
//    }

//    @Test
//    void testGetRequestById() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
//        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
//        final ItemRequestResponseDto requestDto = ItemRequestResponseDto.toItemRequestDto(request);
//        requestDto.setItems(List.of(itemMapper.toItemDtoList(item)));
//
//        ResponseEntity<ItemRequestResponseDto> actualRequest = requestService.getRequestById(1L);
//
//        Assertions.assertEquals(requestDto, actualRequest);
//    }
}