drop table if exists bookings;
DROP TABLE if exists cars;
DROP TABLE if exists customers;


create table cars(
    licencePlateNumber varchar (255) not null,
    brand varchar (255)  not null,
    model varchar (255) not null,
    category varchar(20) not null,
    id varchar(255) not null PRIMARY KEY
);
create table customers (
    fullName varchar (255) not null,
    address varchar (255) not null,
    dateBirth date not null,
    id varchar(255)not null PRIMARY KEY ,
    amountExpended double precision
);
create table bookings(
    customerID varchar(255),
    carID varchar(255),
    firstDate datetime not null ,
    finalDate datetime not null ,
    purchaseDate datetime not null,
    status varChar(40),
    id varchar(255) not null PRIMARY KEY,
    rental double precision,
    deposit double precision,


    CONSTRAINT fk_bookings_car FOREIGN KEY (carID) REFERENCES cars(id),
    CONSTRAINT fk_bookings_customer FOREIGN KEY (customerID) REFERENCES customers(id)

)


