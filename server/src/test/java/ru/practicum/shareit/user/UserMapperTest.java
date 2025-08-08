package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserDtoSimple;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void toEntity_shouldMapChangeUserDtoToUser() {
        ChangeUserDto dto = new ChangeUserDto(1L, "user@yandex.ru", "Name");

        User user = userMapper.toEntity(dto);

        assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru")
                .hasFieldOrPropertyWithValue("name", "Name");
    }

    @Test
    void toUserDto_shouldMapUserToUserResponseDto() {
        User user = new User(1L, "Name", "user@yandex.ru");

        UserResponseDto dto = userMapper.toUserDto(user);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru")
                .hasFieldOrPropertyWithValue("name", "Name");
    }

    @Test
    void toUserDtoSimple_shouldMapUserToUserDtoSimple() {
        User user = new User(1L, "Name", "user@yandex.ru");

        UserDtoSimple dto = userMapper.toUserDtoSimple(user);

        assertThat(dto)
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void toUserDtoList_shouldMapUserListToResponseDtoList() {
        List<User> users = List.of(
                new User(1L, "Name1", "user1@yandex.ru"),
                new User(2L, "Name2", "user2@yandex.ru")
        );

        List<UserResponseDto> dto = userMapper.toUserDtoList(users);

        assertThat(dto)
                .hasSize(2)
                .extracting(UserResponseDto::getEmail)
                .containsExactly("user1@yandex.ru", "user2@yandex.ru");
    }

    @Test
    void toEntity_shouldReturnNullWhenInputIsNull() {
        assertThat(userMapper.toEntity(null)).isNull();
    }

    @Test
    void toUserDto_shouldReturnNullWhenInputIsNull() {
        assertThat(userMapper.toUserDto(null)).isNull();
    }

    @Test
    void toUserDtoSimple_shouldReturnNullWhenInputIsNull() {
        assertThat(userMapper.toUserDtoSimple(null)).isNull();
    }

    @Test
    void toUserDtoList_shouldReturnNullWhenInputIsNull() {
        assertThat(userMapper.toUserDtoList(null)).isNull();
    }

    @Test
    void toUserDtoList_shouldHandleEmptyList() {
        assertThat(userMapper.toUserDtoList(List.of())).isEmpty();
    }
}
