CREATE TABLE Users (
    Id serial PRIMARY KEY,
    Login varchar(64) UNIQUE NOT NULL,
    Password varchar(64) NOT NULL
);
