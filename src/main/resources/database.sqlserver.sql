IF OBJECT_ID(N'dbo.teams', N'U') IS NULL
BEGIN
  CREATE TABLE teams
  (
   id INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY,
   name NVARCHAR(512) NOT NULL
  );
END;

IF OBJECT_ID(N'dbo.results', N'U') IS NULL
BEGIN
  CREATE TABLE results
  (
   id INTEGER NOT NULL IDENTITY(1,1) PRIMARY KEY,
   team_id INTEGER NOT NULL FOREIGN KEY REFERENCES teams (id),
   result INTEGER NOT NULL,
   score INTEGER NOT NULL
  );
END;
