package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.OnCreate;
import ru.practicum.shareit.OnUpdate;

@Getter
@Setter
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    @NotNull
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email;

}
