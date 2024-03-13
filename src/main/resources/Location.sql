CREATE TABLE Locations (
    Id serial PRIMARY KEY,
    Name varchar(128) NOT NULL,
    UserId int REFERENCES Users (Id) ON DELETE CASCADE NOT NULL,
    Latitude decimal NOT NULL,
    Longitude decimal  NOT NULL
);
-- **************
CREATE TABLE IF NOT EXISTS public.locations
(
    id integer NOT NULL DEFAULT nextval('locations_id_seq'::regclass),
    latitude double precision,
    longitude double precision,
    name character varying(255) COLLATE pg_catalog."default",
    userid integer,
    CONSTRAINT locations_pkey PRIMARY KEY (id),
    CONSTRAINT constraint_user_location FOREIGN KEY (userid)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.locations
    OWNER to postgres;

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