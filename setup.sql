/*Yiyuan LI*/
/*CSE 414 HW7 - SETUP SQL*/
CREATE TABLE RentalPlan
(
	pid INTEGER PRIMARY KEY,
	name VARCHAR(20),
	maxNumber INTEGER,
	monthlyFee INTEGER
);

CREATE TABLE Customer
(
	id INTEGER PRIMARY KEY,
	pid INTEGER,
	login VARCHAR(20),
	password VARCHAR(10),
	fname VARCHAR(20),
	lname VARCHAR(20),
	FOREIGN KEY(pid) REFERENCES RentalPlan(pid)
);

CREATE TABLE Rental
(
	cid INTEGER,
	mid INTEGER,
	status INTEGER,
	dataAndTime datetime,
	PRIMARY KEY (cid, mid, dataAndTime),
	FOREIGN KEY(cid) REFERENCES Customer(id)
);


/*seems using primary key alredy created a cluster index*/
/*CREATE CLUSTERED INDEX INDEX_0 ON Customer (id);
CREATE CLUSTERED INDEX INDEX_1 ON RentalPlan (pid);
CREATE CLUSTERED INDEX INDEX_2 ON Rental (cid, mid, dataAndTime);*/

/*Populate RentalPlan*/
INSERT INTO RentalPlan VALUES (0, 'Basic', 1, 5);
INSERT INTO RentalPlan VALUES (1, 'Rental Plus', 3, 10);
INSERT INTO RentalPlan VALUES (2, 'Super Access', 5, 15);
INSERT INTO RentalPlan VALUES (3, 'King Aceess', 99, 50);

/*Populate Customer*/
INSERT INTO Customer VALUES (0, 0, 'abc', '123', 'Mike', 'Jerry');
INSERT INTO Customer VALUES (1, 1, 'ghi', '123', 'Tom', 'Jerry');
INSERT INTO Customer VALUES (2, 0, 'pqr', '123', 'Hank', 'Tom');
INSERT INTO Customer VALUES (3, 1, 'lno', '123', 'Britney', 'Jake');
INSERT INTO Customer VALUES (4, 2, 'def', '123', 'Britney', 'Ana');
INSERT INTO Customer VALUES (5, 1, 'vwx', '123', 'Britney', 'Jerry');
INSERT INTO Customer VALUES (6, 3, 'yl22', '123', 'Mike', 'Li');
INSERT INTO Customer VALUES (7, 3, 'lyyjohn45', '123', 'Michael', 'Lee');

/*Populate Rental*/
INSERT INTO Rental VALUES (0, 60, 1, '5/23/2013 12:08:25 AM');
INSERT INTO Rental VALUES (0, 63, 0, '5/26/2013 12:08:25 AM');
INSERT INTO Rental VALUES (7, 90, 1, '5/21/2013 12:08:25 AM');
INSERT INTO Rental VALUES (7, 34, 0, '1/26/2013 12:08:25 AM');
INSERT INTO Rental VALUES (7, 35, 0, '5/26/2013 12:08:25 AM');
INSERT INTO Rental VALUES (7, 60, 0, '5/26/2013 12:08:25 AM');
INSERT INTO Rental VALUES (2, 36, 0, '5/24/2013 12:08:25 AM');

/*drop table statement
DROP TABLE Rental;
DROP TABLE Customer;
DROP TABLE RentalPlan;
*/
