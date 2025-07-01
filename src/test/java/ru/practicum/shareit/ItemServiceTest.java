package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceTest extends ShareItApplicationTests {

    @Autowired
    ItemRepository itemRepository;

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    void createItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void searchItem() {
    }

    @Test
    void deleteItemById() {
    }

    @Test
    void getAllUserItems() {
    }
}