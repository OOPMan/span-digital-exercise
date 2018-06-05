CREATE TABLE IF NOT EXISTS teams
(
 id SERIAL NOT NULL PRIMARY KEY,
 name VARCHAR(512) NOT NULL
);
-- STATEMENT MARKER
CREATE TABLE IF NOT EXISTS results
(
 id SERIAL NOT NULL PRIMARY KEY,
 team_id INTEGER NOT NULL REFERENCES teams(id),
 result INTEGER NOT NULL,
 score INTEGER NOT NULL
);
-- STATEMENT MARKER
CREATE INDEX IF NOT EXISTS results_teams_id_fk ON results (team_id);
