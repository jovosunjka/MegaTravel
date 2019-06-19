CREATE TABLE User (
  Id INTEGER NOT NULL AUTO_INCREMENT,
  Username VARCHAR(100),
  Firstname VARCHAR(100),
  Lastname VARCHAR(100),
  -- Representative_Id INTEGER,
  -- Headquarter_Id INTEGER,
  -- Chief_Id INTEGER,
  Email VARCHAR(64),
  -- Phone VARCHAR(32),
  Password varchar(120),
  PRIMARY KEY(Id)
);