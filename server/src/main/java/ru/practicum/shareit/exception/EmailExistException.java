package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class EmailExistException extends RuntimeException {

    public EmailExistException(String message) {
        super(message);
    }
}
