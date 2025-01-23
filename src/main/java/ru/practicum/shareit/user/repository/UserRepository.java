package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Repository
public class UserRepository {
    private Long userId = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> uniqueEmailSet = new HashSet<>();


    public User createUser(User user) {
        String email = user.getEmail();
        if (uniqueEmailSet.contains(email)) {
            throw new EmailExistException("Пользователь с такой почтой уже зарегистрирован");
        }
        user.setId(++userId);
        users.put(user.getId(), user);
        uniqueEmailSet.add(email);
        return user;
    }

    public User updateUser(User user) {
        String email = user.getEmail();
        Long userId = user.getId();
        User oldUser = users.get(userId);
        if (!email.equals(oldUser.getEmail())) {
            if (uniqueEmailSet.contains(email)) {
                throw new EmailExistException("Пользователь с такой почтой уже зарегистрирован");
            }
            uniqueEmailSet.remove(oldUser.getEmail());
            uniqueEmailSet.add(email);
        }
        users.put(userId, user);
        return user;
    }

    public User getUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

}
