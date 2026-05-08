package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import java.util.List;

@Service
public class RatingService {
    private final RatingDbStorage ratingDbStorage;

    public RatingService(RatingDbStorage ratingDbStorage) {
        this.ratingDbStorage = ratingDbStorage;
    }

    public List<RatingDto> getListRatings() {
        return ratingDbStorage.getAllRatings().stream()
                .map(RatingMapper::maptoRatingDto)
                .toList();
    }


    public RatingDto findRatingByID(int ratingId) {
        Rating rating = ratingDbStorage.findRatingById(ratingId)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));

        return RatingMapper.maptoRatingDto(rating);
    }

}
