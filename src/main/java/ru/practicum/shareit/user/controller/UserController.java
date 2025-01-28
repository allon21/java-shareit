package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto user) {
        log.info("Получен POST-запрос к /items с телом: {}", user);
        UserDto newUser = userService.createUser(user);
        return ResponseEntity.ok().body(newUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto user, @PathVariable Long id) {
        log.info("Получен Patch-запрос к /items с userId = {} и телом: {}", id, user);
        UserDto newUser = userService.updateUser(id, user);
        return ResponseEntity.ok().body(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        log.info("Получен Delete-запрос к /items/{}", id);
        UserDto newUser = userService.deleteUser(id);
        return ResponseEntity.ok().body(newUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("Получен GET-запрос к /items/{}", id);
        UserDto newUser = userService.getUserById(id);
        return ResponseEntity.ok().body(newUser);
    }


}
