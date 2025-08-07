package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserTestDto;
import ru.practicum.shareit.user.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = {UserController.class})
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserResponseDto changeUserDto = new UserResponseDto(1L, "ru.practicum.shareit.user@yandex.ru", "User");
    private final User user = new User(1L, "User", "ru.practicum.shareit.user@yandex.ru");

    @Test
    void testSuccessCreateUser() throws Exception {
        when(userService.createUser(any(ChangeUserDto.class)))
                .thenReturn(ResponseEntity.ok(changeUserDto));

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("ru.practicum.shareit.user@yandex.ru"));

        verify(userService, times(1)).createUser(any(ChangeUserDto.class));
    }

    @Test
    void testSuccessUpdateUser() throws Exception {
        UserTestDto updateRequest = new UserTestDto("updated@yandex.ru", "Updated");
        UserResponseDto updatedUser = new UserResponseDto(1L, "updated@yandex.ru", "Updated");

        when(userService.updateUser(any(ChangeUserDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(updatedUser));

        mockMvc.perform(patch("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@yandex.ru"));

        verify(userService, times(1)).updateUser(any(ChangeUserDto.class), anyLong());
    }

    @Test
    void testSuccessGetUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok(changeUserDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("ru.practicum.shareit.user@yandex.ru"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testSuccessDeleteUserById() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUserById(1L);
    }
}