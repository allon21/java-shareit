package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Получен POST-запрос к /items с телом: {}", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto user, @PathVariable Long id) {
        log.info("Получен Patch-запрос к /items с userId = {} и телом: {}", id, user);
        return userService.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Получен GET-запрос к /items/{}", id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Получен Delete-запрос к /items/{}", id);
        userService.deleteUser(id);
    }
}
