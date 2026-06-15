package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaDbStorage mpaDbStorage;

    @GetMapping
    public List<MpaRating> getAllMpa() {
        log.info("Получен запрос на получение всех MPA рейтингов");
        return mpaDbStorage.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable @Positive int id) {
        log.info("Получен запрос на получение MPA рейтинга с id: {}", id);
        return mpaDbStorage.getMpaRatingById(id);
    }
}
