package request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestController requestController;

    @Test
    void testSuccessAdRequest() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Description");

        ItemRequestResponseDto expectedResponse = new ItemRequestResponseDto();
        expectedResponse.setId(1L);
        expectedResponse.setDescription("Description");

        when(requestService.addRequest(userId, requestDto)).thenReturn(ResponseEntity.ok(expectedResponse));

        ItemRequestResponseDto result = requestController.addRequest(userId, requestDto).getBody();

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(requestService).addRequest(userId, requestDto);
    }

    @Test
    void testSuccessGetRequestsByUser() {
        Long userId = 1L;
        ItemRequestResponseDto request1 = new ItemRequestResponseDto();
        request1.setId(1L);
        ItemRequestResponseDto request2 = new ItemRequestResponseDto();
        request2.setId(2L);
        List<ItemRequestResponseDto> expectedRequests = List.of(request1, request2);

        when(requestService.getRequestsByUser(userId)).thenReturn(ResponseEntity.ok(expectedRequests));

        List<ItemRequestResponseDto> result = requestController.getRequestsByUser(userId).getBody();

        assertEquals(2, result.size());
        assertEquals(expectedRequests, result);
        verify(requestService).getRequestsByUser(userId);
    }

    @Test
    void testSuccessGetRequestsByUser_shouldReturnEmptyList() {
        Long userId = 1L;
        when(requestService.getRequestsByUser(userId)).thenReturn(ResponseEntity.ok(List.of()));

        List<ItemRequestResponseDto> result = requestController.getRequestsByUser(userId).getBody();

        assertTrue(result.isEmpty());
        verify(requestService).getRequestsByUser(userId);
    }

    @Test
    void testSuccessGetAllAvailableRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        ItemRequestResponseDto request1 = new ItemRequestResponseDto();
        request1.setId(1L);
        ItemRequestResponseDto request2 = new ItemRequestResponseDto();
        request2.setId(2L);
        List<ItemRequestResponseDto> expectedRequests = List.of(request1, request2);

        when(requestService.getAllAvailableRequests(from, size, userId))
                .thenReturn(ResponseEntity.ok(expectedRequests));

        ResponseEntity<List<ItemRequestResponseDto>> response =
                requestController.getAllAvailableRequests(from, size, userId);
        List<ItemRequestResponseDto> result = response.getBody();

        assertEquals(2, result.size());
        assertEquals(expectedRequests, result);
        verify(requestService).getAllAvailableRequests(from, size, userId);
    }

    @Test
    void testSuccessGetRequestsById() {
        Long requestId = 1L;
        ItemRequestResponseDto expectedRequest = new ItemRequestResponseDto();
        expectedRequest.setId(requestId);
        expectedRequest.setDescription("Description");

        when(requestService.getRequestById(requestId)).thenReturn(ResponseEntity.ok(expectedRequest));

        ItemRequestResponseDto result = requestController.getRequestsById(requestId).getBody();

        assertNotNull(result);
        assertEquals(expectedRequest, result);
        verify(requestService).getRequestById(requestId);
    }
}