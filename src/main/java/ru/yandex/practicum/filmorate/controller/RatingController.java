package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.services.RatingService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<RatingDto> getRatings() {
        return ratingService.getListRatings();
    }

    @GetMapping("/{ratingId}")
    public RatingDto getGenre(@PathVariable("ratingId") int ratingId) {
        return ratingService.findRatingByID(ratingId);
    }

}
