CREATE TABLE IF NOT EXISTS public.users
(
    id integer NOT NULL DEFAULT nextval('users_id_seq'::regclass),
    login character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT uk_ow0gan20590jrb00upg3va2fn UNIQUE (login)
);
CREATE TABLE public.locations
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
);
CREATE TABLE public.sessions
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    userid integer,
    expiresat timestamp without time zone,
    CONSTRAINT sessions_pkey PRIMARY KEY (id),
    CONSTRAINT sessions_userid_fkey FOREIGN KEY (userid)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)
