--drop schema Restaurant; <- if this isn't commented out use a # instead

Create Database Restaurant;

USE Restaurant;

CREATE TABLE Restaurant (
	Name      Char(20) 	       NOT NULL,
    Phone     Char(12)  	   NOT NULL,
	Street    Char(40)         NOT NULL,
    City      Char(10)         NOT NULL,
    State     Char(2)          NOT NULL,
	CONSTRAINT      Restaurant_PK    PRIMARY KEY (Name)
	);

CREATE TABLE Building_Cost (
    Restaurant_Number Char(20)     NOT NULL,
    Rent_Cost       Integer      NOT NULL,
    Month           CHAR(3)      NOT NULL,
    CONSTRAINT      Building_Cost_PK        PRIMARY KEY(Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );

CREATE TABLE Insurance (
    Type            CHAR(30)     NOT NULL,
    Restaurant_Number Char(20)     NOT NULL,
    Cost            Integer      NOT NULL,
    Renewal_Date    CHAR(10)     NOT NULL,
    CONSTRAINT      Insurance_PK    PRIMARY KEY (Type,Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship1 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );




CREATE TABLE Electric (
    Company         CHAR(30)    NOT NULL,
    Restaurant_Number Char(20)     NOT NULL,
    Cost            Integer     NOT NULL,
    CONSTRAINT      Electric_PK PRIMARY KEY (Company,Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship3 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );

CREATE TABLE Gas (
    Company         CHAR(30)    NOT NULL,
    Restaurant_Number Char(20)     NOT NULL,
    Cost            Integer     NOT NULL,
    CONSTRAINT      Gas_PK PRIMARY KEY (Company,Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship4 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );


CREATE TABLE Water (
    Company         CHAR(30)    NOT NULL,
    Restaurant_Number Char(20)     NOT NULL,
    Cost            Integer     NOT NULL,
    CONSTRAINT      Water_PK PRIMARY KEY (Company,Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship5 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );

CREATE TABLE Utilities_Cost (
    Restaurant_Number Char(20)     NOT NULL,
    Cost            Integer     NOT NULL,
    CONSTRAINT      Utilities_Cost_PK    PRIMARY KEY (Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship2 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );


CREATE TABLE Payroll (
    EmployeeID          CHAR(20)    NOT NULL,
    Restaurant_Number   CHAR(20)        NOT NULL,
    Pay_Rate            Integer     NOT NULL,
    PRIMARY KEY(EmployeeID)
    );

CREATE TABLE Employee (
    EmployeeID          CHAR(20)        NOT NULL,
    Restaurant_Number   CHAR(20)        NOT NULL,
    Phone           Char(12)  	        NOT NULL,
	Street          Char(40)            NOT NULL,
    City            Char(20)            NOT NULL,
    State           Char(2)             NOT NULL,
    CONSTRAINT 		Employee_PK         PRIMARY KEY (EmployeeID),
    CONSTRAINT 		Payroll_Relationship1 FOREIGN KEY (EmployeeID)
			REFERENCES 	   Payroll (EmployeeID)
    );
#
CREATE TABLE Daily_Operation_Cost (
    Restaurant_Number Char(20)     NOT NULL,
    Employee_Cost   Integer     NOT NULL,
    Building_Cost  Integer     NOT NULL,
    CONSTRAINT Daily_Operation_Cost_PK  PRIMARY KEY(Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship6 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );

CREATE TABLE Certifications (
    EmployeeID      CHAR(20)         NOT NULL,
    Type            CHAR(20)         NOT NULL,
    Renewal_Date    CHAR(10)         NOT NULL,
    Expire_Soon     CHAR(1)         Not NULL,
    CONSTRAINT 		Certifications_PK     PRIMARY KEY(EmployeeID),
    CONSTRAINT 		Payroll_Relationship2  FOREIGN KEY (EmployeeID)
			REFERENCES 	   Payroll (EmployeeID)
    );
#
CREATE TABLE Position (
    EmployeeID      CHAR(20)         NOT NULL,
    Title               CHAR(20)    NOT NULL,
    CONSTRAINT 		Position_PK     PRIMARY KEY(EmployeeID),
    CONSTRAINT 		Payroll_Relationship3  FOREIGN KEY (EmployeeID)
			REFERENCES 	   Payroll (EmployeeID)
    );

CREATE TABLE Orders (
    Ticket_Number       CHAR(20)     NOT NULL,
    Restaurant_Number   Char(20)     NOT NULL,
    Total_Sale          Float      NOT NULL,
    CONSTRAINT 		    Orders_PK       PRIMARY KEY(Ticket_Number,Restaurant_Number),
    CONSTRAINT          Ticket_Number_U UNIQUE (Ticket_Number),
    CONSTRAINT 		Restaurant_Number_Relationship7 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name)
    );
#
CREATE TABLE Meal_Order (
    Ticket_Number       CHAR(20)    NOT NULL,
    Restaurant_Number   Char(20)    NOT NULL,
    Meal_Name           CHAR(20)    NOT NULL,
    Quantity            Integer     NOT NULL,
    CONSTRAINT 		Meal_Order_PK   PRIMARY KEY(Meal_Name,Ticket_Number,Restaurant_Number),
    CONSTRAINT 		Restaurant_Number_Relationship9 FOREIGN KEY (Restaurant_Number)
			REFERENCES 	   Restaurant(Name),
    CONSTRAINT      Ticket_Number_Relationship FOREIGN KEY (Ticket_Number)
              REFERENCES Orders(Ticket_Number)
);

CREATE TABLE Meal (
    Meal_Name   CHAR(20)    NOT NULL,
    Cost        Integer    NOT NULL,
    CONSTRAINT 		Meal_Name_PK PRIMARY KEY(Meal_Name)
);
CREATE TABLE Inventory (
     Product_Name       CHAR(20)    NOT NULL,
     CONSTRAINT      Inventory_PK PRIMARY KEY(Product_Name)
);

CREATE TABLE Products (
    Product_Name                CHAR(20)    NOT NULL,
    Restaurant_Number   Char(20)    NOT NULL,
    Quantity_On_Hand            DOUBLE     NOT NULL,
    CONSTRAINT 	Ingredient_PK PRIMARY KEY(Product_Name, Restaurant_Number),
    CONSTRAINT Inventory_Relationship2 FOREIGN KEY(Product_Name)
                REFERENCES Inventory(Product_Name)
);
CREATE TABLE Ingredient_Used (
    Meal_Name           CHAR(20)    NOT NULL,
    Product_Name        CHAR(20)    NOT NULL,
    Quantity_Used       DOUBLE     NOT NULL,
    CONSTRAINT 		Ingredient_Used_PK PRIMARY KEY(Meal_Name,Product_Name)
);



CREATE TABLE Payment_Type (
    Ticket_Number   CHAR(20)    NOT NULL,
    Restaurant_Number   Char(20)    NOT NULL,
    Cash            Integer     NOT NULL,
    Card            Integer     NOT NULL,
    Split_Check     CHAR(1)     NOT NULL,
    CONSTRAINT      Payment_Type_PK PRIMARY KEY(Ticket_Number,Restaurant_Number)
);



CREATE TABLE Card_Sales (
    Ticket_Number    CHAR(20)       NOT NULL,
    Restaurant_Number   Char(20)    NOT NULL,
    Customer_Name       Char(20)     NOT NULL,
    Card_Number      CHAR(16)       NOT NULL,
    Exp_Date         CHAR(5)        NOT NULL,
    Date_Of_Sale     CHAR(10)       NOT NULL,
    CONSTRAINT      Payment_Type_PK PRIMARY KEY(Ticket_Number,Restaurant_Number)
);

CREATE TABLE Ingredient_Source (
    Product_Name        CHAR(20)     NOT NULL,
    Company             CHAR(30)     NOT NULL,
    Unit_Price            Integer      NOT NULL,
    Unit_Size           CHAR(5)      NOT NULL,
    Shipping_Cost     Integer        NOT NULL,
    CONSTRAINT      Ingredient_Source_PK  PRIMARY KEY(Product_Name,Company)
);

Insert into Restaurant values ('Dicks1', '2063045025', '124220 Pacific Hwy S',
                               'Kent', 'WA');

Insert into Restaurant values ('Dicks2', '2063231300', '115 Broadway E',
                               'Seattle', 'WA');

Insert into Restaurant values ('Dicks3', ' 2062855155', '500 Queen Anne Ave N',
                               'Seattle', 'WA');

Insert into Building_Cost values ('Dicks1', 5500, 'Jan');
#
Insert into Insurance values ('Property Insurance', 'Dicks1', '120','5/19/2024');
Insert into Insurance values ('Disability Insurance', 'Dicks1', '130','5/19/2024');
Insert into Insurance values ('Unemployment Insurance', 'Dicks1', '160','5/19/2024');

Insert into Insurance values ('Property Insurance', 'Dicks2', '120','5/19/2024');
Insert into Insurance values ('Disability Insurance', 'Dicks2', '130','5/19/2024');
Insert into Insurance values ('Unemployment Insurance', 'Dicks2', '160','5/19/2024');

Insert into Insurance values ('Property Insurance', 'Dicks3', '120','5/19/2024');
Insert into Insurance values ('Disability Insurance', 'Dicks3', '130','5/19/2024');
Insert into Insurance values ('Unemployment Insurance', 'Dicks3', '160','5/19/2024');

Insert into Electric values ('Puget Sound Energy', 'Dicks1', 87);

Insert into Gas values ('Puget Sound Energy', 'Dicks1', 87);

Insert into Water values ('Highline Water District', 'Dicks1', 90);

Insert into Electric values ('Puget Sound Energy', 'Dicks2', 102);

Insert into Gas values ('Puget Sound Energy', 'Dicks2', 87);

Insert into Water values ('SPU', 'Dicks2', 110);

Insert into Electric values ('Puget Sound Energy', 'Dicks3', 101);

Insert into Gas values ('Puget Sound Energy', 'Dicks3', 77);

Insert into Water values ('SPU', 'Dicks3', 99);

#
Insert into Utilities_Cost values('Dicks1',
                                  (select Cost
                                  from Water where Restaurant_Number = 'Dicks1')+(select Cost
                                  from Electric where Restaurant_Number = 'Dicks1')+(select Cost
                                  from Gas where Restaurant_Number = 'Dicks1'));

Insert into Utilities_Cost values('Dicks2',
                                  (select Cost
                                  from Water where Restaurant_Number = 'Dicks2')+(select Cost
                                  from Electric where Restaurant_Number = 'Dicks2')+(select Cost
                                  from Gas where Restaurant_Number = 'Dicks2'));

Insert into Utilities_Cost values('Dicks3',
                                  (select Cost
                                  from Water where Restaurant_Number = 'Dicks3')+(select Cost
                                  from Electric where Restaurant_Number = 'Dicks3')+(select Cost
                                  from Gas where Restaurant_Number = 'Dicks3'));
#
INSERT into Payroll values('100', 'Dicks1',25);
INSERT into Payroll values('101','Dicks1', 15);
INSERT into Payroll values('102','Dicks1',18);

INSERT into Payroll values('200', 'Dicks2',13);
INSERT into Payroll values('201', 'Dicks2',5);
INSERT into Payroll values('202','Dicks2',8);

INSERT into Payroll values('300', 'Dicks3',30);
INSERT into Payroll values('301', 'Dicks3',18);
INSERT into Payroll values('302','Dicks3',20);

INSERT into Employee values('100', 'Dicks1', '1234567890', '14 Matts Hill Rd', 'Kent', 'WA');
INSERT into Employee values('101', 'Dicks1', '1234567890', '24800 110TH CT SE','Kent', 'WA');
INSERT into Employee values('102', 'Dicks1','1234567890', '700 REITEN RD','Kent','WA');

INSERT into Employee values('200', 'Dicks2', '1234654674', '400 Street North', 'Tacoma', 'WA');
INSERT into Employee values('201', 'Dicks2', '1234565659', '320th North Way','Federal Way', 'WA');
INSERT into Employee values('202', 'Dicks2', '1234566645', '700 Route 9','Snohomish','WA');

INSERT into Employee values('300', 'Dicks3', '2536815432', '565 Clearwater', 'Des Moines', 'WA');
INSERT into Employee values('301', 'Dicks3', '5096871321', '986 Slater Street','Fife', 'WA');
INSERT into Employee values('302', 'Dicks3', '3678952621', '12607 Lane ct.','Renton','WA');


insert into Daily_Operation_Cost values('Dicks1',
                                        (select SUM(Pay_Rate)*8 from Payroll
                                         where Restaurant_Number = 'Dicks1'),
                                        (select SUM(Cost)/30 from Utilities_Cost
                                         where Restaurant_Number = 'Dicks1'));

insert into Daily_Operation_Cost values('Dicks2',
                                        (select SUM(Pay_Rate)*8 from Payroll
                                         where Restaurant_Number = 'Dicks2'),
                                        (select SUM(Cost)/30 from Utilities_Cost
                                         where Restaurant_Number = 'Dicks2'));

insert into Daily_Operation_Cost values('Dicks3',
                                        (select SUM(Pay_Rate)*8 from Payroll
                                         where Restaurant_Number = 'Dicks3'),
                                        (select SUM(Cost)/30 from Utilities_Cost
                                         where Restaurant_Number = 'Dicks3'));

INSERT into Certifications values('100', 'Food Worker Permit', '10/26/2023', 'Y');
INSERT into Certifications values('101', 'Food Worker Permit', '10/27/2023','Y');
INSERT into Certifications values('102', 'Food Worker Permit', '10/28/2023', 'N');

INSERT into Certifications values('200', 'Food Worker Permit', '08/02/2023', 'N');
INSERT into Certifications values('201', 'Food Worker Permit', '02/14/2023','N');
INSERT into Certifications values('202', 'Food Worker Permit', '04/16/2023', 'N');
INSERT into Certifications values('300', 'Food Worker Permit', '01/30/2022', 'Y');
INSERT into Certifications values('301', 'Food Worker Permit', '11/07/2023','N');
INSERT into Certifications values('302', 'Food Worker Permit', '10/28/2023', 'N');

INSERT into Position values('100', 'Cook');
INSERT into Position values('101', 'Server');
INSERT into Position values ('102', 'Hostess');

INSERT into Position values('200', 'Cook');
INSERT into Position values('201', 'Server');
INSERT into Position values ('202', 'Hostess');

INSERT into Position values('300', 'Cook');
INSERT into Position values('301', 'Server');
INSERT into Position values ('302', 'Hostess');


Insert into Meal values ('Deluxe',5);
Insert into Meal values ('Special',4);
Insert into Meal values ('Cheeseburger',3);
Insert into Meal values ('Hamburger',2);

Insert into Inventory values ('1/8lbs Patty');
Insert into Inventory values ('Cheese');
Insert into Inventory values ('Lettuce');
Insert into Inventory values ('Mayo');
Insert into Inventory values ('Pickle Relish');
Insert into Inventory values ('Mustard');
Insert into Inventory values ('Ketchup');


Insert into Products values ('1/8lbs Patty','Dicks2',400);
Insert into Products values ('Cheese','Dicks2',500);
Insert into Products values ('Lettuce','Dicks2',400);
Insert into Products values ('Mayo','Dicks2',4);
Insert into Products values ('Pickle Relish','Dicks2',2);
Insert into Products values ('Mustard','Dicks2',3);
Insert into Products values ('Ketchup','Dicks2',4);

Insert into Products values ('1/8lbs Patty','Dicks1',700);
Insert into Products values ('Cheese','Dicks1',200);
Insert into Products values ('Lettuce','Dicks1',400);
Insert into Products values ('Mayo','Dicks1',3);
Insert into Products values ('Pickle Relish','Dicks1',2);
Insert into Products values ('Mustard','Dicks1',3);
Insert into Products values ('Ketchup','Dicks1',4);

Insert into Products values ('1/8lbs Patty','Dicks3',400);
Insert into Products values ('Cheese','Dicks3',500);
Insert into Products values ('Lettuce','Dicks3',400);
Insert into Products values ('Mayo','Dicks3',4);
Insert into Products values ('Pickle Relish','Dicks3',2);
Insert into Products values ('Mustard','Dicks3',3);
Insert into Products values ('Ketchup','Dicks3',4);

Insert into Ingredient_Used values ('Deluxe', '1/8lbs Patty', 2);
Insert into Ingredient_Used values ('Deluxe', 'Cheese', 1);
Insert into Ingredient_Used values ('Deluxe', 'Lettuce', 0.05);
Insert into Ingredient_Used values ('Deluxe', 'Mayo', 0.01);
Insert into Ingredient_Used values ('Deluxe', 'Pickle Relish', 0.02);

Insert into Ingredient_Used values ('Special', '1/8lbs Patty', 1);
Insert into Ingredient_Used values ('Special', 'Lettuce', 0.05);
Insert into Ingredient_Used values ('Special', 'Mayo', 0.01);
Insert into Ingredient_Used values ('Special', 'Pickle Relish', 0.02);

Insert into Ingredient_Used values ('Cheeseburger', '1/8lbs Patty', 1);
Insert into Ingredient_Used values ('Cheeseburger', 'Cheese', 1);
Insert into Ingredient_Used values ('Cheeseburger', 'Ketchup', 0.01);
Insert into Ingredient_Used values ('Cheeseburger', 'Mustard', 0.01);

Insert into Ingredient_Used values ('Hamburger', '1/8lbs Patty', 1);
Insert into Ingredient_Used values ('Hamburger', 'Ketchup', 0.01);
Insert into Ingredient_Used values ('Hamburger', 'Mustard', 0.01);


Insert into Ingredient_Source values ('1/8lbs Patty','BeefIsUs', 150, '400', 10);
Insert into Ingredient_Source values ('Cheese','CheezusJesus', 100, '100', 5);
Insert into Ingredient_Source values ('Lettuce','LettuceFree', 10, '10', 2);
Insert into Ingredient_Source values ('Mayo','AnythingButMiracleWhip', 10, '10', 2);
Insert into Ingredient_Source values ('Mustard','SpicyYellowStuff', 8 , '5', 3);
Insert into Ingredient_Source values ('Ketchup','WorstKetchup', 8 , '5', 3);
Insert into Ingredient_Source values ('Pickle Relish','RelishSuccess', 4 , '4', 2);
