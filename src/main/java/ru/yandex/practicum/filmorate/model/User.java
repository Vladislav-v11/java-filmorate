package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    Long id;

    @NotBlank(message = "Адрес электронной почты должен быть указан")
    @Email(message = "Электронная почта должна быть формата электронного адреса")
    String email;

    @NotBlank
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    String login;
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
