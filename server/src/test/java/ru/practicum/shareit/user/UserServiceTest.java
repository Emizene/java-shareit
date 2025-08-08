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
import org.springframework.http.HttpStatusCode;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.ChangeUserDto;
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
    void testDeleteUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUserById(1L);
        verify(userRepository, times(1))
                .delete(any());
    }
}