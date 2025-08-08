package ru.practicum.shareit.user;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final Long id = 1L;
    private final ChangeUserDto userDto = new ChangeUserDto(id, "user@yandex.ru", "User");
    private final User user = new User(id, "User", "user@yandex.ru");

    @Test
    void testSuccessGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var targetUsers = userService.getAllUsers().getBody();

        Assertions.assertNotNull(targetUsers);
        Assertions.assertEquals(1, targetUsers.size());
        verify(userRepository, times(1))
                .findAll();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testGetUserById_whenUserFound_thenReturnedUser() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var actualUser = userService.getUserById(id);

        assertThat(actualUser.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = actualUser.getBody();
        assertThat(body.getId()).isEqualTo(1);
        AssertionsForInterfaceTypes.assertThat(body.getEmail()).isEqualTo("user@yandex.ru");
        AssertionsForInterfaceTypes.assertThat(body).isNotNull();
    }

    @Test
    void testGetUserById_whenUserNotFound_thenExceptionThrown() {
        when((userRepository).findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testCreateUser_whenUserNameValid_thenSavedUser() {
        when(userRepository.save(any())).thenReturn(user);

        var actualUser = userService.createUser(userDto);

        assertThat(actualUser.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        var body = actualUser.getBody();
        assertThat(body.getId()).isEqualTo(1);
        AssertionsForInterfaceTypes.assertThat(body.getEmail()).isEqualTo("user@yandex.ru");
        AssertionsForInterfaceTypes.assertThat(body).isNotNull();
    }

    @Test
    void testCreateUser_whenUserEmailDuplicate_thenNotSavedUser() {
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userDto));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testCreateUser_whenEmailUnique_shouldCreateUser() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<UserResponseDto> response = userService.createUser(userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(userDto.getEmail());
        verify(userRepository).save(any());
    }

    @Test
    void testCreateUser_whenEmailExists_shouldThrowConflictException() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testUpdateUser_whenUserFound_thenUpdatedOnlyAvailableFields() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var actualUser = userService.updateUser(userDto, id);

        assertThat(actualUser.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        var body = actualUser.getBody();
        assertThat(body.getId()).isEqualTo(1);
        AssertionsForInterfaceTypes.assertThat(body.getEmail()).isEqualTo("user@yandex.ru");
        AssertionsForInterfaceTypes.assertThat(body).isNotNull();
        verify(userRepository, times(1))
                .findById(user.getId());
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, id));
    }

    @Test
    void updateUser_whenEmailChangedToExisting_shouldThrowConflictException() {
        User existingUser = new User(id, "Old Name", "old@email.com");
        User duplicateUser = new User(2L, "Other User", "new@email.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(true);

        ChangeUserDto updateDto = new ChangeUserDto(null, "new@email.com", null);

        assertThrows(ConflictException.class, () -> userService.updateUser(updateDto, id));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void updateUser_whenEmailChangedToUnique_shouldUpdateEmail() {
        User existingUser = new User(id, "Old Name", "old@email.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChangeUserDto updateDto = new ChangeUserDto(null, "new@email.com", null);

        ResponseEntity<UserResponseDto> response = userService.updateUser(updateDto, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEmail()).isEqualTo("new@email.com");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void updateUser_whenNameChanged_shouldUpdateName() {
        User existingUser = new User(id, "Old Name", "user@yandex.ru");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChangeUserDto updateDto = new ChangeUserDto(null, null, "New Name");

        ResponseEntity<UserResponseDto> response = userService.updateUser(updateDto, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("New Name");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void updateUser_whenNoChanges_shouldReturnOriginal() {
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChangeUserDto updateDto = new ChangeUserDto(null, user.getEmail(), user.getName());

        ResponseEntity<UserResponseDto> response = userService.updateUser(updateDto, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getBody().getName()).isEqualTo(user.getName());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUserById(1L);
        verify(userRepository, times(1))
                .delete(any());
    }
}