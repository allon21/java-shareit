package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    final UserServiceImpl userService;
    Long userId;

    @BeforeEach
    public void setUp() {
        UserDto userDto = UserDto.builder()
                .email("user@test.com")
                .name("Test User")
                .build();

        userDto = userService.createUser(userDto);
        userId = userDto.getId();
    }

    @Test
    public void createUserTest() {
        UserDto newUser = UserDto.builder()
                .email("newuser@test.com")
                .name("New User")
                .build();

        UserDto createdUser = userService.createUser(newUser);
        assertNotNull(createdUser.getId());
        assertEquals("New User", createdUser.getName());
        assertEquals("newuser@test.com", createdUser.getEmail());
    }

    @Test
    public void updateUserTest() {
        UserDto updatedUserDto = UserDto.builder()
                .email("updateduser@test.com")
                .name("Updated User")
                .build();

        UserDto updatedUser = userService.updateUser(userId, updatedUserDto);
        assertEquals("Updated User", updatedUser.getName());
        assertEquals("updateduser@test.com", updatedUser.getEmail());
    }

    @Test
    public void deleteUserTest() {
        UserDto deletedUser = userService.deleteUser(userId);
        assertEquals("Test User", deletedUser.getName());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void getUserByIdTest() {
        UserDto user = userService.getUserById(userId);
        assertEquals("Test User", user.getName());
        assertEquals("user@test.com", user.getEmail());
    }

    @Test
    public void findAllUsersTest() {
        Collection<UserDto> users = userService.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(user -> user.getId().equals(userId)));
    }

    @Test
    public void duplicateEmailTest() {
        UserDto duplicateUser = UserDto.builder()
                .email("user@test.com")
                .name("Duplicate User")
                .build();

        assertThrows(EmailExistException.class, () -> userService.createUser(duplicateUser));
    }

    @Test
    public void creatingUserWithEmptyEmailShouldThrowValidationException() {
        UserDto invalidUser = UserDto.builder()
                .email("")
                .name("Invalid User")
                .build();

        assertThrows(ValidationException.class, () -> userService.createUser(invalidUser));
    }
}