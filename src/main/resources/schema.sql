-- Another Spring Boot feature demonstrated in this guide is the ability to initialize the schema on startup:
drop table BOOKINGS if exists;
create table BOOKINGS(ID serial, FIRST_NAME varchar(5) NOT NULL);