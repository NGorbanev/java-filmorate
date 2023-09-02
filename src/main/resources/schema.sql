DROP TABLE IF EXISTS FILM_GENRES , FILMS , FRIENDSHIP , GENRES , LIKES , MPA_RATINGS , USERS CASCADE;

CREATE TABLE IF NOT EXISTS mpa_ratings (
	rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	rating_name varchar(100) NOT NULL,
	rating_description varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres(
	genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	genre_name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	film_name varchar(100) NOT NULL,
	film_description varchar(255),
	release_date date NOT NULL CONSTRAINT check_release_date CHECK (release_date > '1895-12-28'),
	duration INTEGER NOT NULL CONSTRAINT check_duration CHECK (duration > 0),
	mpa INTEGER REFERENCES mpa_ratings(rating_id)
);

CREATE TABLE IF NOT EXISTS users(
	user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email varchar(255) NOT NULL,
	login varchar(100) UNIQUE,
	name varchar(100) DEFAULT 'Anonymous user',
	birthday date CONSTRAINT check_bday CHECK (birthday < CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS likes(
	film_id INTEGER REFERENCES films(film_id),
	user_id INTEGER REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS friendship(
	friend_1_id INTEGER REFERENCES users(user_id),
	friend_2_id INTEGER REFERENCES users(user_id)
	--friendship_acceptance boolean maybe i dont need it
);

CREATE TABLE IF NOT EXISTS film_genres(
	film_id INTEGER REFERENCES films(film_id),
	genre_id INTEGER REFERENCES genres(genre_id)
);