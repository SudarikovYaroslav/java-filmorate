package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @Autowired


    @GetMapping()
    public List<Mpa> getAllMpaRatings() {
        return mpaService.findAllMpaRatings();
    }

    @GetMapping("/{id}")
    public Mpa getMpaRatingById(@PathVariable long id) {
        checkId(id);
        return mpaService.findMpaRatingById(id);
    }

    private void checkId(long id) {
        if (id < 0) throw new MpaNotFoundException("mpa id не может быть отрицательным");
    }
}
