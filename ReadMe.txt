Dicks Restaurant Sample Database

Authors: Ian McLean and Patrick Tibbals

1) This project will require the JDBC plugin found online.
    we provided it in our src code, to configure it in Intellij go to:
    File>Project Structure>Dependencies and then click the .jar file within the mysql folder.

    If you're not using Intellij then just make sure it's added to your class path.

2) MySql is the DataBase management system that is used.

3) In order to run the program the database must me initialized within the local host computer. Port: 3306.

4) You must have MySql server installed and running this database.

5) *MAKE SURE TO PASS THE PASSWORD TO YOU LOCAL HOST AS A COMMAND LINE ARGUMENT IN THE PROGRAM*

6) After which the main method can be ran to prompt the UI interactions for our database.

Database Options: All prompts are clear and concise as to the purpose and intended input.

    - If it asks for an exact amount, then do so. Regardless, the program handles bad input and
    re-prompts the user.

            1 - Place Order
                * Used to place meal orders and combine them into singular tickets per customer
            2 - Order Inventory
                * Orders extra inventory only when quantity on hand drops below a certain amount
            3 - Update Inventory
                * Allow custom entry for orders received in case the received quantity
                  is different then the requested amount
            4 - Change employee information - Key is by employee ID
                                              Current employees = (100,101,102,200,201,202,300,301,302)
                    1 - Add new employee
                    2 - Remove employee
                    3 - Check certification
                    4 - Check pay rate
                    5 - Check position
            5 - Close out for the day
                * Sum up total sales and operation costs for the day



***** If program suffers catastrophic failure --- Run the provided sql and uncomment the drop schema line *****