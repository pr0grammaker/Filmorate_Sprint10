--DELETE FROM film_likes;
--DELETE FROM friendships;
--DELETE FROM films;
--DELETE FROM users;
--DELETE FROM genres;
--DELETE FROM ratings;

ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE ratings ALTER COLUMN id RESTART WITH 1;
-- =========================
-- GENRES
-- =========================
MERGE INTO genres (name) KEY(name) VALUES
('Экшен'),
('Комедия'),
('Драма'),
('Ужасы'),
('Триллер'),
('Фэнтези'),
('Научная фантастика'),
('Романтика'),
('Анимация'),
('Документальный'),
('Исторический');

-- =========================
-- RATINGS
-- =========================
MERGE INTO ratings (name) KEY(name) VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17');

---- =========================
---- USERS (10 человек)
---- =========================
--MERGE INTO users (email, login, name, birthday) KEY(id) VALUES
--('ivan.petrov@mail.ru', 'ivan_petrov', 'Иван Петров', '1995-03-12'),
--('anna.smirnova@mail.ru', 'anna_s', 'Анна Смирнова', '1998-07-22'),
--('dmitry.kuznetsov@mail.ru', 'dima_k', 'Дмитрий Кузнецов', '1992-11-05'),
--('olga.ivanova@mail.ru', 'olga_i', 'Ольга Иванова', '2000-01-17'),
--('sergey.volkov@mail.ru', 'sergey_v', 'Сергей Волков', '1989-06-30'),
--('elena.sokolova@mail.ru', 'elena_s', 'Елена Соколова', '1997-09-14'),
--('nikita.popov@mail.ru', 'nikita_p', 'Никита Попов', '2001-12-03'),
--('irina.kozlova@mail.ru', 'irina_k', 'Ирина Козлова', '1994-04-25'),
--('alexey.fedorov@mail.ru', 'alex_f', 'Алексей Фёдоров', '1990-08-19'),
--('tatiana.morozova@mail.ru', 'tanya_m', 'Татьяна Морозова', '1996-02-10');
--
---- =========================
---- FILMS (10 фильмов)
---- =========================
--INSERT INTO films (name, description, release_date, duration, genre_id, rating_id) VALUES
--('Тёмный город', 'Фантастический триллер о городе без памяти', '1998-02-27', 100, 7, 3),
--('Любовь и кофе', 'Романтическая история в большом городе', '2020-06-14', 95, 8, 2),
--('Последний бой', 'Экшен о солдате, спасающем мир', '2015-09-10', 120, 1, 4),
--('Смех до слёз', 'Лёгкая комедия про друзей', '2018-03-01', 90, 2, 1),
--('Дом страха', 'Хоррор про заброшенный дом', '2012-10-31', 105, 4, 4),
--('История империи', 'Документальный фильм о древних цивилизациях', '2010-05-20', 80, 11, 1),
--('Космическая одиссея', 'Путешествие по галактике', '2021-12-01', 130, 7, 3),
--('Мир драконов', 'Фэнтези о магии и героях', '2019-07-07', 140, 6, 3),
--('Тайный агент', 'Шпионский триллер', '2016-11-11', 110, 5, 3),
--('Жизнь художника', 'Драма о творческом пути', '2013-04-18', 115, 3, 2);
--
---- =========================
---- FRIENDSHIPS
---- =========================
--INSERT INTO friendships (user_id, friend_id, status) VALUES
--(1, 2, 'CONFIRMED'),
--(2, 1, 'CONFIRMED'),
--(1, 3, 'CONFIRMED'),
--(3, 1, 'CONFIRMED'),
--(4, 5, 'PENDING'),
--(5, 4, 'PENDING'),
--(6, 7, 'CONFIRMED'),
--(7, 6, 'CONFIRMED'),
--(8, 9, 'CONFIRMED'),
--(9, 8, 'CONFIRMED'),
--(10, 1, 'PENDING');
--
---- =========================
---- FILM LIKES
---- =========================
--INSERT INTO film_likes (film_id, user_id) VALUES
--(1, 1),
--(1, 2),
--(2, 1),
--(2, 3),
--(3, 4),
--(3, 5),
--(4, 6),
--(5, 7),
--(6, 8),
--(7, 9),
--(8, 10),
--(9, 2),
--(10, 3),
--(7, 1),
--(8, 2);