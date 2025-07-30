package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ChangeUserDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Valid
@RequestMapping("/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody ChangeUserDto user) {
        log.info("Create user {}", user);
        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody ChangeUserDto user) {
        log.info("Update user {}", user);
        return userClient.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Get user with id={}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        log.info("Delete user with id={}", userId);
        return userClient.deleteUserById(userId);
    }
}
