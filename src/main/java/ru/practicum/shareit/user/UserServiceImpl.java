package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.ChangeUserDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ResponseEntity<UserResponseDto> createUser(ChangeUserDto user) {
        log.debug("Создание нового пользователя: email={}", user.getEmail());
        List<User> allUsers = userRepository.findAll().stream()
                .toList();
        boolean emailExists = allUsers.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            log.warn("Этот имейл уже используется: {}", user.getEmail());
            throw new ConflictException("Этот имейл уже используется");
        }

        User entity = userMapper.toEntity(user);
        userRepository.save(entity);
        log.info("Успешное создание пользователя: ID={}", user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserDto(entity));
    }

    @Override
    @Transactional
    public ResponseEntity<UserResponseDto> updateUser(ChangeUserDto user, Long userId) {
        log.debug("Обновление существующего пользователя с ID {}", userId);

//        List<User> allUsers = userRepository.findAll().stream()
//                .toList();
//
//        User updatedUser = allUsers.stream()
//                .filter(u -> Objects.equals(u.getId(), user.getId()))
//                .findFirst()
//                .orElseThrow(() -> {
//                    log.error("Пользователь с ID {} не найден для обновления", user.getId());
//                    return new NotFoundException("Пользователь с id " + user.getId() + " не найден");
//                });

        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для обновления", user.getId());
                    return new NotFoundException("Пользователь с id " + user.getId() + " не найден");
                });

        if (user.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            boolean emailExists = userRepository.existsByEmail(user.getEmail());
            if (emailExists) {
                log.warn("Пользователь с email {} уже существует", user.getEmail());
                throw new ConflictException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            log.debug("Обновление email пользователя с {} на {}", updatedUser.getEmail(), user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }

        if (user.getName() != null && !updatedUser.getName().equals(user.getName())) {
            log.debug("Обновление имени пользователя с {} на {}", updatedUser.getName(), user.getName());
            updatedUser.setName(user.getName());
        }

        userRepository.save(updatedUser);
        log.info("Пользователь с ID {} успешно обновлен", user.getId());

        return ResponseEntity.ok().body(userMapper.toUserDto(updatedUser));
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(userId)));

        log.info("Найден пользователь: ID={}", userId);

        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID %s не найден".formatted(userId)));

//        TODO добавить сюда удаление вещей
//        // Удалить связи, где пользователь добавлен в друзья другими
//        Set<User> usersWhoAdded = userRepository.findUsersWhoAddedAsFriend(userId);
//        usersWhoAdded.forEach(u -> u.getFriends().remove(user));
//        userRepository.saveAll(usersWhoAdded);
//
//        // Удалить дружеские связи пользователя
//        user.getFriends().clear();
//
//        // Удалить лайки
//        List<Film> likedFilmsCopy = new ArrayList<>(user.getLikedFilms());
//        likedFilmsCopy.forEach(film -> {
//            film.getUsersWithLikes().remove(user);
//        });
//        filmRepository.saveAll(likedFilmsCopy);

        userRepository.delete(user);

        log.info("Пользователь с ID: ID={} удалён", userId);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        log.debug("Запрос всех пользователей");
        List<User> users = userRepository.findAll().stream()
                .toList();
        log.info("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(userMapper.toUserDtoList(users));
    }
}
