CREATE TABLE artist (
	artist TEXT NOT NULL,
	PRIMARY KEY(artist)
);

INSERT INTO artist (artist) VALUES('侯耀文');
INSERT INTO artist (artist) VALUES('石富宽');

CREATE TABLE podcast (
	artist TEXT,
	track TEXT
);

CREATE UNIQUE INDEX idx_podcast ON podcast (artist, track);