package ru.practicum.shareit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.UserRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest extends ShareItApplicationTests {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void getAllUsers() {
    }
}