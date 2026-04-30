package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Film {
    Long id;

    @NotBlank(message = "Название должно быть заполнено")
    String name;

    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    String description;

    @NotNull(message = "Дата релиза должна быть указна")
    LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    int duration;
}
