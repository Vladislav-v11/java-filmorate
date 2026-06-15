package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;

    @NotBlank(message = "Название должно быть заполнено")
    private String name;

    @Size(max = 200, message = "Описание не должно быть длиннее 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть указна")
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private MpaRating mpa;

    private Set<Genre> genres = new HashSet<>();

    private Set<Long> likes = new HashSet<>();
}
