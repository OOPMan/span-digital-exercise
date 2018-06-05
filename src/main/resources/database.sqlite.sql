CREATE TABLE IF NOT EXISTS teams
(
 id INTEGER PRIMARY KEY AUTOINCREMENT,
 name varchar(512) NOT NULL
);
-- STATEMENT MARKER
CREATE TABLE results
(
 id integer PRIMARY KEY AUTOINCREMENT NOT NULL,
 team_id integer NOT NULL,
 result integer NOT NULL,
 score integer NOT NULL,
 CONSTRAINT results_teams_id_fk FOREIGN KEY (team_id) REFERENCES teams (id)
);
