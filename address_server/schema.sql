drop table if exists addresses;
create table addresses (
  id integer primary key autoincrement,
  address string not null,
  nickname string not null,
  latitude number not null,
  longitude number not null
);