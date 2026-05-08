
package ru.yandex.practicum.filmorate.model;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    private long id;
    private String name;
}

//package ru.yandex.practicum.filmorate.model;
//
//import lombok.Builder;
//import lombok.Getter;
//
//import java.util.Arrays;
//
//@Getter
//public enum Genre {
//    ACTION(1, "Экшен"),
//    COMEDY(2, "Комедия"),
//    DRAMA(3, "Драма"),
//    HORROR(4, "Ужасы"),
//    THRILLER(5, "Триллер"),
//    FANTASY(6, "Фэнтези"),
//    SCI_FI(7, "Научная фантастика"),
//    ROMANCE(8, "Романтика"),
//    ANIMATION(9, "Анимация"),
//    DOCUMENTARY(10, "Документальный"),
//    HISTORY(11, "Исторический");
//
//    private final String russianName;
//    private final int id;
//
//    Genre(int id, String russianName) {
//        this.id = id;
//        this.russianName = russianName;
//    }
//
//    public static Genre fromId(int genreId) {
//        return Arrays.stream(values())
//                .filter(r -> r.id == genreId)
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalArgumentException("Unknown rating id: " + genreId)
//                );
//    }
//}
