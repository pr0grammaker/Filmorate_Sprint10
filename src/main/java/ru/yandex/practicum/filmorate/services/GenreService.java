package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public List<GenreDto> getListGenres() {
        return genreDbStorage.getAllGenres().stream()
                .map(GenreMapper::maptoGenreDto)
                .toList();
    }


    public GenreDto findGenreByID(int genreId) {
        Genre genre = genreDbStorage.getGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));

        return GenreMapper.maptoGenreDto(genre);
    }
}
