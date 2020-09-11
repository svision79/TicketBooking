CREATE DATABASE IF NOT EXISTS parking;
CREATE TABLE cars(Floor int ,
 Slot int ,
 RegNo varchar(32) ,
 Color varchar(32) ,
 Ticket varchar(32),
 PRIMARY KEY (Ticket) );