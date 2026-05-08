CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    genre_id INT,
    rating_id INT,
    CONSTRAINT films_genre_fk FOREIGN KEY (genre_id)
        REFERENCES genres(id)
        ON DELETE SET NULL,
    CONSTRAINT films_rating_fk FOREIGN KEY (rating_id)
        REFERENCES ratings(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT friendships_user_fk FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT friendships_friend_fk FOREIGN KEY (friend_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT film_likes_film_fk FOREIGN KEY (film_id)
        REFERENCES films(id)
        ON DELETE CASCADE,
    CONSTRAINT film_likes_user_fk FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);