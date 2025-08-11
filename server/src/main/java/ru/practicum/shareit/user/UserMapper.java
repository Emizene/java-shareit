package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserDtoSimple;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(ChangeUserDto changeUserDto);

    UserResponseDto toUserDto(User user);

    UserDtoSimple toUserDtoSimple(User user);

    List<UserResponseDto> toUserDtoList(List<User> user);
}
