package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Valid
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody ChangeUserDto user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody ChangeUserDto user, @PathVariable Long userId) {
        return userService.updateUser(user, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        return userService.deleteUserById(userId);
    }
}
