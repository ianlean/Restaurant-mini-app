import java.sql.*;
import java.util.*;
import java.util.Date;

/*
    The driver class for this program

    Authors: Ian McLean and Patrick Tibbals
*/
public class Main {
    /*
        The URL linking to the database server
    */
    public static final String URL = "jdbc:mysql://localhost:3306/Restaurant";

    /*
        Connection object to the database
    */
    public static Connection dbConnection = null;

    /*
        A sql statement object for queries
    */
    public static Statement myStmt;
    /*
        A sql statement object for queries (some how we ended up with two)
    */
    public static Statement Stmt;
    /*
        A sql statement object for updates and adding format specifiers to them
   */
    public static PreparedStatement prepStmt;
    /*
        A result set to keep track of queried data
    */
    public static ResultSet myRs;
    /*
        The ticket number that the restaurant is currently on
    */
    public static int currentTicket = 11;
    /*
        The current restaurant the user is working from
    */
    public static int currentRestaurant;

    /*
        Total cost of an order, resets to 0 when done
    */
    public static int totalCost = 0;

    /*
        Boolean to keep track of when an order is complete
    */
    public static boolean orderDone = false;

    /*
        Keeps track of product orders, resets when done
    */
    public static ArrayList<String> orderList = new ArrayList<>();

    /*
        Scanner for user input
    */
    public static Scanner s = new Scanner(System.in);

    /*
        The starting point for this program

        @param args - command line arguments, used to collect the password for the database
     */
    public static void main(String[] args) throws SQLException {

        dbConnection = DriverManager.getConnection(URL, "root",args[0]);
        myStmt = dbConnection.createStatement();
        System.out.println("Hello, please enter the Restaurant number you are working from: (1-3) ");
        getRest();
        boolean exit = false;
        while (!exit) {
            System.out.println("Pick Desired Action:");
            System.out.println("""
                    1 - Place Order
                    2 - Order Inventory
                    3 - Update Inventory
                    4 - Change employee information
                    5 - Close out for the day
                    6 - Exit program""");

            insureValidity();
            switch (s.nextInt()) {
                case 1 -> placeOrder();
                case 2 -> orderInventory();
                case 3 -> updateProduct();
                case 4 -> addRemoveEmployee();
                case 5 -> closeOut();
                case 6 -> {
                    exit = true;
                    System.out.println("Good bye");
                }
                default -> System.out.println("Try valid input");
            }
        }
    }

    /*
        Closes out the restaurant, tallies total costs for the day
    */
    public static void closeOut(){
        System.out.println("Gathering days sale");
        Date today = new Date();


        try {
            myRs = myStmt.executeQuery("select SUM(Total_Sale) as Total_Sales from orders");
            showOutput(myRs);
            prepStmt = dbConnection.prepareStatement("Select Employee_Cost + Building_Cost as Operation_Cost from Daily_Operation_Cost where Restaurant_Number = ?");
            prepStmt.setString(1, "Dicks"+ currentRestaurant);
            myRs = prepStmt.executeQuery();
            showOutput(myRs);



            myStmt.execute("DELETE FROM meal_order");
            myStmt.execute("DELETE FROM orders");
            System.exit(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        Updates inventory after a meal has been placed

        @param meal - The meal that was ordered
    */
    public static void updateInventoryQuantity(String meal){
        try {
            prepStmt = dbConnection.prepareStatement("SELECT Product_Name, Quantity_Used FROM Ingredient_Used WHERE Meal_Name = ?;");
            prepStmt.setString(1, meal);
            myRs = prepStmt.executeQuery();

            //Custom loop to get numbers for each ingredit for each meal?????????????????
            while (myRs.next()) {
                    String productName = myRs.getString(1);
                    String quantityUsed = myRs.getString(2);
                    prepStmt = dbConnection.prepareStatement("SELECT Quantity_On_Hand FROM Products WHERE Product_Name = ? and Restaurant_Number = ?;");
                    prepStmt.setString(1, productName);
                prepStmt.setString(2, "Dicks"+ currentRestaurant);
                    ResultSet tempRS = prepStmt.executeQuery();
                    tempRS.next();
                    String quantityOnHand = tempRS.getString(1);
                    prepStmt = dbConnection.prepareStatement("Update Products Set Quantity_On_Hand = ? Where Product_Name = ? and Restaurant_Number = ?;");
                    Double temp = Double.parseDouble(quantityOnHand)-Double.parseDouble(quantityUsed);
                    prepStmt.setDouble(1, temp);
                    prepStmt.setString(2, productName);
                prepStmt.setString(3, "Dicks"+ currentRestaurant);
                    prepStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /*
         For employee queries and updates
    */
    public static void addRemoveEmployee(){
        System.out.println("What would you like to do with Employee:");
        System.out.println("1 - Add new employee");
        System.out.println("2 - Remove employee");
        System.out.println("3 - Check certification");
        System.out.println("4 - Check pay rate");
        System.out.println("5 - Check position");
        System.out.println("6 - Exit");
        insureValidity();

        switch (s.nextInt()) {
            case 1 -> {
                try {
                    addEmp();
                } catch (SQLException e) {
                    System.out.println("Entered invalid employee information");
                }
            }
            case 2 -> removeEmp();
            case 3 -> checkCert();
            case 4 -> checkPay();
            case 5 -> checkPosition();
            case 6 -> { break;}

            default -> System.out.println("Enter valid input");
        }

    }

    /*
        Updates inventory after a meal has been placed

        @param str - Checks if a string can be turned into a number
    */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /*
        Adds an employee by requesting data
    */
    public static void addEmp() throws SQLException {
        try {
            System.out.println("Enter Employee Number (1-999)");
            String employee = s.next();
            System.out.println(employee);
            while (!isNumeric(employee) || (Integer.parseInt(employee) > 999 || Integer.parseInt(employee)<0)) {
                System.out.println("Must be at least 3 numbers (1-999)");
                employee = s.next();
            }

            String id = employee;
            System.out.println("Enter Phone (no dashes or space): ");
            String phone = s.next();
            System.out.println("Enter the street they live on (forty character limit): ");
            s.nextLine();
            String street = s.nextLine();
            System.out.println("Enter the city (20 character limit): ");
            String city = s.next();
            System.out.println("Enter abbreviated state: ");
            String state = validState();
            System.out.println("Enter wage: ");
            insureValidity();
            int pay = s.nextInt();

            prepStmt = dbConnection.prepareStatement("INSERT INTO Payroll VALUES(?,?,?)");
            prepStmt.setString(1, id);
            prepStmt.setString(2, "Dicks"+currentRestaurant);
            prepStmt.setString(3, String.valueOf(pay));
            prepStmt.executeUpdate();

            prepStmt = dbConnection.prepareStatement("INSERT INTO Employee VALUES(?,?,?,?,?,?)");
            prepStmt.setString(1, id);
            prepStmt.setString(2, "Dicks"+currentRestaurant);
            prepStmt.setString(3, phone);
            prepStmt.setString(4, street);
            prepStmt.setString(5, city);
            prepStmt.setString(6, state);

            prepStmt.executeUpdate();

            prepStmt = dbConnection.prepareStatement("INSERT INTO Certifications VALUES(?,?,?,?)");
            System.out.println("Enter the date they got their food service card (mm/dd/yyyy): ");
            String date= s.next();
            prepStmt.setString(1, id);
            prepStmt.setString(2,"Food service card");
            prepStmt.setString(3, date);
            prepStmt.setString(4, "n");
            prepStmt.executeUpdate();

            prepStmt = dbConnection.prepareStatement("INSERT INTO Position VALUES(?,?)");
            s.nextLine();
            System.out.println("Enter their job title (i.e dish washer): ");
            String title = s.nextLine();
            prepStmt.setString(1, id);
            prepStmt.setString(2,title);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("this employee already exists");
            e.printStackTrace();

        }

    }

    /*
        Updates inventory after a meal has been placed

        @return If the state provided is abbreviated
    */
    public static String validState() {
        String temp = s.next();
        while(temp.length() != 2) {
            System.out.println("Must be abbreviated: ");
            temp = s.next();
        }
        return temp;
    }

    /*
        Removes an employee by requesting valid ID
    */
    public static void removeEmp(){
        System.out.println("Enter ID:");
        String emp = s.next();

        try {
            prepStmt = dbConnection.prepareStatement("select * from employee where EmployeeID = ? ");
            prepStmt.setString(1,emp);
            if(prepStmt.execute()) {

                prepStmt = dbConnection.prepareStatement("Delete from position where EmployeeID = ?");
                prepStmt.setString(1,emp);
                prepStmt.execute();

                prepStmt = dbConnection.prepareStatement("Delete from certifications where EmployeeID = ?");
                prepStmt.setString(1,emp);
                prepStmt.execute();

                prepStmt = dbConnection.prepareStatement("Delete from employee where EmployeeID = ? ");
                prepStmt.setString(1,emp);
                prepStmt.execute();

                prepStmt = dbConnection.prepareStatement("Delete from payroll where EmployeeID = ? ");
                prepStmt.setString(1,emp);
                prepStmt.execute();


            }
        } catch (SQLException e) {
            System.out.println("This employee doesn't exist");
        }
    }

    /*
        Checks employee certification given a valid ID
    */
    public static void checkCert(){
        System.out.println("Enter ID:");
        String emp = s.next();
        try {
            prepStmt = dbConnection.prepareStatement("select * from employee where EmployeeID = ? ");
            prepStmt.setString(1,emp);
            if(prepStmt.execute()) {
                prepStmt = dbConnection.prepareStatement("select Type,Renewal_Date from certifications where EmployeeID = ?");
                prepStmt.setString(1,emp);
                showOutput(prepStmt.executeQuery());
            }
        } catch (SQLException e) {
            System.out.println("this employee does not exists");
            e.printStackTrace();
        }
    }

    /*
        Prints out result set

        @param rs - The result set that is being displayed
    */
    public static void showOutput(ResultSet rs) {
        ResultSetMetaData rsmd = null;
        try {
            rsmd = rs.getMetaData();
            printData(rs, rsmd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        Removes an employee by requesting valid ID
    */
    private static void printData(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
        System.out.println("-------------------------------------------------------------------------------");

        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                //if (i > 1) System.out.print(" -  ");
                String columnValue = rs.getString(i);

                System.out.print(rsmd.getColumnName(i) + ": "+ columnValue + " ");
            }
            System.out.println("");
        }
        System.out.println("-------------------------------------------------------------------------------");

    }

    /*
       Checks employee pay given valid ID
    */
    public static void checkPay(){
        System.out.println("Enter ID:");
        String emp = s.next();
        try {
            prepStmt = dbConnection.prepareStatement("select * from employee where EmployeeID = ? ");
            prepStmt.setString(1,emp);
            if(prepStmt.execute()) {
                prepStmt = dbConnection.prepareStatement("select Pay_Rate from payroll where EmployeeID = ?");
                prepStmt.setString(1,emp);
                showOutput(prepStmt.executeQuery());
            }
        } catch (SQLException e) {
            System.out.println("this employee does not exists");
            e.printStackTrace();
        }
    }
    /*
       Checks an employees position/title given valid ID
    */
    public static void checkPosition(){
        System.out.println("Enter ID:");
        String emp = s.next();
        try {
            prepStmt = dbConnection.prepareStatement("select * from employee where EmployeeID = ? ");
            prepStmt.setString(1,emp);
            if(prepStmt.execute()) {
                prepStmt = dbConnection.prepareStatement("select Title from position where EmployeeID = ?");
                prepStmt.setString(1,emp);
                showOutput(prepStmt.executeQuery());

            }
        } catch (SQLException e) {
            System.out.println("this employee does not exists");
            throw new RuntimeException(e);
        }

    }

    /*
       Gets valid Integer input from the user
    */
    public static void insureValidity() {
        while(!s.hasNextInt()) {
            System.out.println("Enter Valid Choice");
            s.next();
        }
    }

    /*
      Prompts the user for the meals to add to the order
    */
    public static void promptWorker() throws SQLException {
        //ResultSet
        while(!orderDone) {
            insureValidity();
            System.out.println("Enter meal or 5 to exit");
            switch (s.nextInt()) {
                case 1 -> {
                    orderList.add("Hamburger");
                    updateInventoryQuantity("Hamburger");
                    totalCost += 2;
                }
                case 2 -> {
                    orderList.add("Cheeseburger");
                    updateInventoryQuantity("Cheeseburger");
                    totalCost += 3;
                }
                case 3 -> {
                    orderList.add("Special");
                    updateInventoryQuantity("Special");
                    totalCost += 4;
                }
                case 4 -> {
                    orderList.add("Deluxe");
                    updateInventoryQuantity("Deluxe");
                    totalCost += 5;
                }
                case 5 -> orderDone = true;
                default -> {
                    System.out.println("Choose 1 - 4");
                    return;
                }
            }

        }
        orderDone = false;
    }

    /*
       Gets valid restaurant in range
    */
    public static void getRest() {

        insureValidity();
        currentRestaurant = s.nextInt();
        if (currentRestaurant > 3 || currentRestaurant < 1) {
            System.out.println("Not a valid number");
            getRest();
            return;
        }
        System.out.println("You selected Dicks" + currentRestaurant);
    }

    /*
       Order more inventory.
    */
    public static void orderInventory(){
        System.out.println("Product to order:");
        System.out.println("1 - 1/8lbs Patty");
        System.out.println("2 - Cheese");
        System.out.println("3 - Lettuce");
        System.out.println("4 - Ketchup");
        System.out.println("5 - Mayo");
        System.out.println("6 - Mustard");
        System.out.println("7 - Pickle Relish");
        try {
            ResultSet temp;
            while(!orderDone) {
                insureValidity();
                switch (s.nextInt()) {
                    case 1:
                        if(orderProductHelper("1/8lbs Patty", 800)){
                            orderList.add("1/8lbs Patty");
                        }
                        break;
                    case 2:
                        if(orderProductHelper("Cheese", 300)){
                            orderList.add("Cheese");
                        }
                        break;
                    case 3:
                        if(orderProductHelper("Lettuce", 200)){
                            orderList.add("Lettuce");
                        }
                        break;
                    case 4:

                        if(orderProductHelper("Ketchup", 2)){
                            orderList.add("Ketchup");

                        }
                        break;
                    case 5:
                        if(orderProductHelper("Mayo", 2)){
                            orderList.add("Mayo");

                        }
                        break;
                    case 6:
                        if(orderProductHelper("Mustard", 2)){
                            orderList.add("Mustard");

                        }
                        break;
                    case 7:
                        if(orderProductHelper("Pickle Relish", 100)){
                            orderList.add("Pickle Relish");
                        }
                        break;
                    case 8:
                        System.out.println((orderList.toString()));
                        System.out.println("Order total: " + totalCost);
                        orderList = new ArrayList<>();
                    default:
                        System.out.println("Choose 1 - 8");
                        return;
                }
                System.out.println("Enter another order or 8 to display order total");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        orderDone = false;
    }

    /*
       Updates meals/order tables once a meal has been ordered

       @param meal - the meal that was ordered
    */
    public static void orderMealHelper(String meal) throws SQLException {
        prepStmt = dbConnection.prepareStatement("INSERT INTO Meal_Order VALUES(?,?,?,?)");
        prepStmt.setString(1, ticketCorrector());
        prepStmt.setString(2, "Dicks"+currentRestaurant);
        prepStmt.setString(3, meal);
        prepStmt.setInt(4, Collections.frequency(orderList,meal));
        prepStmt.executeUpdate();
    }

    /*
       Queries to make sure the user can order more
       product in their given restaurant.

       @param product - product being ordered.
       @param quantity - The amount being ordered
    */
    public static boolean orderProductHelper(String product,int quantity) throws SQLException {
        ResultSet temp;

        prepStmt = dbConnection.prepareStatement("Select  Quantity_On_Hand From products Where Product_Name = ? and Restaurant_Number = ?" );
        prepStmt.setString(1, product);
        prepStmt.setString(2, "Dicks"+currentRestaurant);
        ResultSet rs = prepStmt.executeQuery();
        rs.next();
        if (Double.parseDouble(rs.getString("Quantity_On_Hand")) < quantity){
            prepStmt = dbConnection.prepareStatement("Select Unit_Price+Shipping_Cost as total From ingredient_source Where Product_Name = ?");
            prepStmt.setString(1, product);

            temp = prepStmt.executeQuery();
            temp.next();
            totalCost += Double.parseDouble(temp.getString("total"));
        } else {
            System.out.println("Too much already on hand");
            return false;
        }
        return true;
    }

    /*
       Updates products once they have arrived.
    */
    public static void updateProduct() throws SQLException{
        String product = ""; Double quantity;
        orderDone = true;
        while(orderDone) {
            System.out.println("Enter Product to update quantity on hand.");
            System.out.println("1 - 1/8lbs Patty");
            System.out.println("2 - Cheese");
            System.out.println("3 - Lettuce");
            System.out.println("4 - Ketchup");
            System.out.println("5 - Mayo");
            System.out.println("6 - Mustard");
            System.out.println("7 - Pickle Relish");
            System.out.println("8 - Exit");

            insureValidity();
            switch (s.nextInt()) {
                case 1:
                    product = ("1/8lbs Patty");
                    break;
                case 2:
                    product = ("Cheese");
                    break;
                case 3:
                    product = ("Lettuce");
                    break;
                case 4:
                    product = ("Ketchup");
                    break;
                case 5:
                    product = ("Mayo");
                    break;
                case 6:
                    product = ("Mustard");
                    break;
                case 7:
                    product = ("Pickle Relish");
                    break;
                case 8:
                    System.out.println("Inventory updated successfully");
                    return;
                default:
                    System.out.println("Choose 1 - 8");
                    break;
            }
            System.out.println("Enter received quantity: ");
            insureValidity();
            quantity = s.nextDouble();
            prepStmt = dbConnection.prepareStatement("Select  Quantity_On_Hand From products Where Product_Name = ?");
            prepStmt.setString(1, product);
            ResultSet rs = prepStmt.executeQuery();
            rs.next();
            quantity += Double.parseDouble(rs.getString("Quantity_On_Hand"));
            prepStmt = dbConnection.prepareStatement("Update Products Set Quantity_On_Hand = ? Where Product_Name = ? and Restaurant_Number = ?");
            prepStmt.setString(1, String.valueOf(quantity));
            prepStmt.setString(2, product);
            prepStmt.setString(3, "Dicks" + currentRestaurant);
            prepStmt.executeUpdate();
        }

    }

    /*
       Changes the ticket to specified database format
    */
    public static String ticketCorrector(){
        String temp = String.valueOf(currentTicket);
        while (temp.length() < 3){
            temp = "0"+temp;
        }
        return temp;
    }

    /*
       Prompts to place an order, inserts in the orders
       once promptWorker() has run.
    */
    public static void placeOrder() {
        System.out.println("Pick Meal:");
        System.out.println("1 - Hamburger");
        System.out.println("2 - Cheeseburger");
        System.out.println("3 - Special");
        System.out.println("4 - Deluxe");
        System.out.println("5 - Exit ordering");

        try {

            promptWorker();
            prepStmt = dbConnection.prepareStatement("INSERT INTO Orders(Ticket_Number, Restaurant_Number,Total_Sale) VALUES(?,?,?)");
            prepStmt.setString(1, ticketCorrector());
            prepStmt.setString(2, "Dicks"+currentRestaurant);
            prepStmt.setInt(3, totalCost);
            prepStmt.executeUpdate();

            int count = 0;
            myStmt = dbConnection.createStatement();
            if(orderList.contains("Hamburger"))  {
                orderMealHelper("Hamburger");
            }
            if(orderList.contains("Cheeseburger")){
                orderMealHelper("Cheeseburger");
            }
            if(orderList.contains("Special")){
                orderMealHelper("Special");
            }
            if(orderList.contains("Deluxe")){
                orderMealHelper("Deluxe");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        currentTicket++;
        orderList = new ArrayList<>();
    }

    /*
       Reads a data Query

       @param query - the query you want printed out
    */
    public static void readQueryData(String query) throws SQLException {
        myRs = Stmt.executeQuery(query);
        ResultSetMetaData rsmd = myRs.getMetaData();
        printData(myRs, rsmd);
    }
}