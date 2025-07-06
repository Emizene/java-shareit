package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    ResponseEntity<UserResponseDto> createUser(ChangeUserDto user);

    ResponseEntity<UserResponseDto> updateUser(ChangeUserDto user, Long userId);

    ResponseEntity<UserResponseDto> getUserById(Long userId);

    ResponseEntity<Void> deleteUserById(Long userId);

    ResponseEntity<List<UserResponseDto>> getAllUsers();
}