
create table open_weather.public.locations (
            id integer generated by default as identity,
            latitude float(52) not null,
            longitude float(52) not null,
            name varchar(255) not null,
            user_id integer not null,
            primary key (id)
);
create table open_weather.public.sessions (
             id varchar(255),
             expires_at timestamp(6) not null,
             user_id integer not null,
             primary key (id)
);
create table open_weather.public.users (
             id integer generated by default as identity,
             login varchar(64) not null check ( length(login) > 5 ),
             password varchar(64) not null check (length(password) > 5),
             primary key (id)
);
alter table if exists open_weather.public.users
    add constraint UK_ow0gan20590jrb00upg3va2fn unique (login);
alter table if exists open_weather.public.locations
    add constraint FKatow05gq6bh4cuqqfyh1if4gi
        foreign key (user_id)
            references open_weather.public.users;
alter table if exists open_weather.public.sessions
    add constraint FKhhicn3brukxnn876dpg8b6mp1
        foreign key (user_id)
            references open_weather.public.users;