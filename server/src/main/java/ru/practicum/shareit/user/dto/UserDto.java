package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private long id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Email
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

}
