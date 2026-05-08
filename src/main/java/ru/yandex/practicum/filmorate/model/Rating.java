package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rating {
    private long id;
    private String name;
}


//
//import lombok.Getter;
//
//import java.util.Arrays;
//
//@Getter
//public enum Rating {
//    G(1, "Нет возрастных ограничений"),
//    PG(2, "Рекомендуется смотреть с родителями"),
//    PG_13(3, "Детям до 13 лет просмотр не желателен"),
//    R(4, "До 17 лет — только в присутствии взрослого"),
//    NC_17(5, "До 18 лет просмотр запрещён");
//
//    private final String description;
//    private final int id;
//
//    Rating(int id, String description) {
//        this.id = id;
//        this.description = description;
//    }
//
//    public static Rating fromId(int id) {
//        return Arrays.stream(values())
//                .filter(r -> r.id == id)
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalArgumentException("Unknown rating id: " + id)
//                );
//    }
//}
