package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review add(@Valid @RequestBody Review review) throws StorageException {
        return reviewService.save(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) throws StorageException {
        return reviewService.update(review);
    }

    @GetMapping
    public Collection<Review> get(@RequestParam(defaultValue = "0") int filmId,
                                  @RequestParam(defaultValue = "10") int count) {
        return reviewService.findAllByFilmId(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) throws StorageException {
        return reviewService.findReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteLike(@PathVariable long id) {
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addLike(id, userId);
    }
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.addDislike(id, userId);
    }
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteLike(id, userId);
    }
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteLikeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
