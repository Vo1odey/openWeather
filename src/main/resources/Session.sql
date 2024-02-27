CREATE TABLE Sessions (
    Id varchar(255) PRIMARY KEY,
    UserId int REFERENCES Users (Id) ON DELETE CASCADE,
    ExpiresAt time
);

/*

[2024-01-23 15:54:17] Connected
open_weather> ALTER TABLE locations
                  DROP CONSTRAINT "fkatow05gq6bh4cuqqfyh1if4gi"
[2024-01-23 15:54:17] completed in 30 ms
open_weather.public> ALTER TABLE locations
                         ADD CONSTRAINT constraint_user_location
                             FOREIGN KEY (userid) REFERENCES users(id) ON DELETE CASCADE
[2024-01-23 15:55:58] completed in 44 ms

 */