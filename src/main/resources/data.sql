MERGE INTO mpa_ratings (mpa_id, name, description) KEY (mpa_id) VALUES
(1, 'G', 'Фильм демонстрируется без ограничений'),
(2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
(3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
(4, 'R', 'Лицам до 17 лет обязательно присутствие взрослого'),
(5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

MERGE INTO genres (genre_id, name) KEY (genre_id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO friendship_status (status_id, name) KEY (status_id) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED');