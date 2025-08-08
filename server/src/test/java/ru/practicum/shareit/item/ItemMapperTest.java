package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDtoSimple;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RequestMapper requestMapper;

    @Test
    void testToEntity_shouldMapItemRequestDtoToEntity() {
        ItemRequestDto dto = new ItemRequestDto(1L, "Description");

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
                .created(Instant.now())
                .build();

        ItemRequestResponseDto dto = requestMapper.toDto(request);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", "Description")
                .hasFieldOrProperty("created");
    }

    @Test
    void testToDtoWithItems_shouldMapWithItemsList() {
        User owner = new User(1L, "Owner", "owner@yandex.ru");
        ItemRequest request = ItemRequest.builder().id(1L).description("Description").build();
        List<Item> items = List.of(
                new Item(1L, "Name1", "Description1", true, owner, request),
                new Item(2L, "Name2", "Description2", true, owner, request)
        );

        when(itemMapper.toItemDtoSimple(ArgumentMatchers.any(Item.class)))
                .thenAnswer(inv -> {
                    Item item = inv.getArgument(0);
                    return new ItemDtoSimple(item.getId(), item.getName());
                });

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
}
