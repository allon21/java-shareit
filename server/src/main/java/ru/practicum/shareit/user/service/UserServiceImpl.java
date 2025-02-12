package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.INSTANCE.toUser(userDto);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailExistException("Почта уже используется");
        }
        return UserMapper.INSTANCE.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + userId));
        if (userDto.getName() == null && userDto.getEmail() == null) {
            throw new IllegalArgumentException("Необходимо передать хотя бы одно поле.");
        }

        if (userDto.getName() != null) {
            newUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            newUser.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.save(newUser);
        return UserMapper.INSTANCE.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto deleteUser(Long userId) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + userId));
        userRepository.deleteById(userId);
        return UserMapper.INSTANCE.toUserDto(newUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.INSTANCE.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Объект не найден, id = " + userId)));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .toList();
    }

}
