package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RequestMapper requestMapper;

    private final Instant now = Instant.now();

    @Test
    void testToEntity_shouldMapItemRequestDtoToEntity() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Description")
                .build();

        ItemRequest entity = requestMapper.toEntity(dto);

        assertThat(entity)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "Description");
    }

    @Test
    void testToDto_shouldMapItemRequestToResponseDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .created(now)
                .build();

        ItemRequestResponseDto dto = requestMapper.toDto(request);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "Description")
                .hasFieldOrPropertyWithValue("created", now);
    }

    @Test
    void testToDtoWithItems_shouldMapWithItemsList() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .created(now)
                .build();

        List<Item> items = List.of(
                new Item(1L, "Name1", "Description1", true, null, request),
                new Item(2L, "Name2", "Description2", true, null, request)
        );

        when(itemMapper.toItemDtoSimple(items.get(0)))
                .thenReturn(new ItemDtoSimple(1L, "Name1"));
        when(itemMapper.toItemDtoSimple(items.get(1)))
                .thenReturn(new ItemDtoSimple(2L, "Name2"));

        ItemRequestResponseDto dto = requestMapper.toDtoWithItems(request, items);

        assertThat(dto.getItems())
                .hasSize(2)
                .extracting(ItemDtoSimple::getName)
                .containsExactly("Name1", "Name2");
    }

    @Test
    void testToItemRequestDtoList_shouldMapListOfEntities() {
        List<ItemRequest> requests = List.of(
                ItemRequest.builder().id(1L).description("Description1").build(),
                ItemRequest.builder().id(2L).description("Description2").build()
        );

        List<ItemRequestResponseDto> dto = requestMapper.toItemRequestDtoList(requests);

        assertThat(dto)
                .hasSize(2)
                .extracting(ItemRequestResponseDto::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void testToDtoWithItems_shouldHandleEmptyItemsList() {
        ItemRequest request = ItemRequest.builder().id(1L).build();

        ItemRequestResponseDto dto = requestMapper.toDtoWithItems(request, List.of());

        assertThat(dto.getItems()).isEmpty();
    }
}
