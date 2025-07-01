package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@SpringBootTest
@AutoConfigureMockMvc
class ShareItApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected MockMvc mockMvc;

}
