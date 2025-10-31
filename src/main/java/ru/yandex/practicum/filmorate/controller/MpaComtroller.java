package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaComtroller {
    public final MpaService mpaService;

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        log.info("Запрос на получение рейтинга ID: {}", id);
        return mpaService.getById(id);
    }

    @GetMapping
    public List<MpaRating> getAllMpa() {
        log.info("Запрос на получение всех рейтингов");
        return mpaService.getAll();
    }
}
