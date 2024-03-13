CREATE TABLE Users (
    Id serial PRIMARY KEY,
    Login varchar(64) UNIQUE NOT NULL,
    Password varchar(64) NOT NULL
);
-- ***********
CREATE TABLE IF NOT EXISTS public.users
(
    id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    login character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT uk_ow0gan20590jrb00upg3va2fn UNIQUE (login)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;