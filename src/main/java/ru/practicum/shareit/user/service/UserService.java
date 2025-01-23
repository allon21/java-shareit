package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Почта не может быть пустой");
        }
        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(Long userId, UserDto user) {
        User newUser = UserMapper.toUser(getUserById(userId));
        User oldUser = userRepository.getUser(userId);
        newUser.setId(oldUser.getId());
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(newUser));
    }

    public UserDto deleteUser(Long id) {
        User user = userRepository.getUser(id);
        userRepository.deleteUser(user.getId());
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.getUser(userId);
        return UserMapper.toUserDto(user);
    }
}
