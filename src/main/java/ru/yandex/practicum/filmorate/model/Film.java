package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializers.DurationMinutesSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;

    /*
     * Добавил сериализацию, чтобы отображение duration было в нормальном виде,
     * а не в виде строки 'PT3M10S', которую возвращает сервер.
     * То есть преобразуем ISO-8601 формат Duration в удобный для фронта вид (секунды).
     * Т.к. этого требует тест
     */
    @JsonSerialize(using = DurationMinutesSerializer.class)
    private Duration duration;

    private Set<Long> likes = new HashSet<>();
}
