import java.sql.*;
import java.util.*;



public class Main {
    public static final String URL = "jdbc:mysql://localhost:3306/Restaurant";
    public static Connection dbConnection = null;

    public static Statement myStmt;
    public static Statement Stmt;
    public static PreparedStatement prepStmt;
    public static ResultSet myRs;

    public static int currentTicket = 11;
    public static int currentRestaurant;

    public static int totalCost = 0;
    public static boolean orderDone = false;
    public static ArrayList<String> orderList = new ArrayList<>();


    public static Scanner s = new Scanner(System.in);
    public Main() throws SQLException {
    }

    public static void main(String[] args) throws SQLException {

        dbConnection = DriverManager.getConnection(URL, "root",args[0]);
        myStmt = dbConnection.createStatement();
        System.out.println("Hello, please enter the Restaurant number you are working from: (1-9) ");
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
                    System.out.println("C-YA LATER FUCK FACE");
                }
                default -> System.out.println("fucking idiot");
            }
        }
    }
    public static void closeOut(){
        System.out.println("Gathering days sale");
        try {
            ResultSet rs = myStmt.executeQuery("select SUM(Total_Sale) from orders");
            showOutput(rs);
            myStmt.execute("DELETE FROM meal_order");
            myStmt.execute("DELETE FROM orders");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
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

            default -> System.out.println("fucking idiot");
        }

    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
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

            prepStmt = dbConnection.prepareStatement("INSERT INTO Payroll VALUES(?,?)");
            prepStmt.setString(1, id);
            prepStmt.setString(2, String.valueOf(pay));
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
            e.printStackTrace();

        }

    }

    public static String validState() {
        String temp = s.next();
        while(temp.length() != 2) {
            System.out.println("I said to abbreviate: ");
            temp = s.next();
        }
        return temp;
    }
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
            e.printStackTrace();
        }
    }

    public static void showOutput(ResultSet rs) {
        ResultSetMetaData rsmd = null;
        try {
            rsmd = rs.getMetaData();
            printData(rs, rsmd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printData(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(" -  ");
                String columnValue = rs.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

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
            e.printStackTrace();
        }
    }
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
            throw new RuntimeException(e);
        }

    }
    public static void insureValidity() {
        while(!s.hasNextInt()) {
            System.out.println("Enter Valid Choice");
            s.next();
        }
    }

    public static void promptWorker() throws SQLException {
        //ResultSet
        while(!orderDone) {
            insureValidity();
            System.out.println("Enter meal or 5 to exit");
            switch (s.nextInt()) {
                case 1 -> {
                    orderList.add("Hamburger");
                    totalCost += 2;
                }
                case 2 -> {
                    orderList.add("Cheeseburger");
                    totalCost += 3;
                }
                case 3 -> {
                    orderList.add("Special");
                    totalCost += 4;
                }
                case 4 -> {
                    orderList.add("Deluxe");
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
                        orderList.add("1/8lbs Patty");
                        orderProductHelper("1/8lbs Patty", 800);
                        break;
                    case 2:
                        orderList.add("Cheese");
                        orderProductHelper("Cheese", 300);
                        break;
                    case 3:
                        orderList.add("Lettuce");
                        orderProductHelper("1/8lbs Patty", 200);
                        break;
                    case 4:
                        orderList.add("Ketchup");
                        orderProductHelper("1/8lbs Patty", 2);
                        break;
                    case 5:
                        orderList.add("Mayo");
                        orderProductHelper("1/8lbs Patty", 2);
                        break;
                    case 6:
                        orderList.add("Mustard");
                        orderProductHelper("1/8lbs Patty", 2);
                        break;
                    case 7:
                        orderList.add("Pickle Relish");
                        orderProductHelper("1/8lbs Patty", 4);
                        break;
                    case 8:
                        System.out.println((orderList.toString()));
                        System.out.println("Order total: " + totalCost);
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

    public static void orderMealHelper(String meal) throws SQLException {
        prepStmt = dbConnection.prepareStatement("INSERT INTO Meal_Order VALUES(?,?,?,?)");
        prepStmt.setString(1, ticketCorrector());
        prepStmt.setString(2, "Dicks"+currentRestaurant);
        prepStmt.setString(3, meal);
        prepStmt.setInt(4, Collections.frequency(orderList,meal));
        prepStmt.executeUpdate();
    }

    public static void orderProductHelper(String product,int quantity) throws SQLException {
        ResultSet temp;

        prepStmt = dbConnection.prepareStatement("Select  Quantity_On_Hand From products Where Product_Name = ?");
        prepStmt.setString(1, product);
        ResultSet rs = prepStmt.executeQuery();
        rs.next();
        if (Integer.parseInt(rs.getString("Quantity_On_Hand")) < quantity){
            orderList.add("1/8lbs Patty");
            prepStmt = dbConnection.prepareStatement("Select Unit_Price+Shipping_Cost as total From ingredient_source Where Product_Name = ?");
            prepStmt.setString(1, product);
            temp = prepStmt.executeQuery();
            temp.next();
            totalCost += Integer.parseInt(temp.getString("total"));
        } else {
            System.out.println("Too much already on hand");
        }
    }

    public static void updateProduct() throws SQLException{
        String product; int quantity;
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
            default:
                System.out.println("Choose 1 - 8");
                return;
        }
        System.out.println("Enter received quantity: ");
        insureValidity();
        quantity = s.nextInt();
        prepStmt = dbConnection.prepareStatement("Select  Quantity_On_Hand From products Where Product_Name = ?");
        prepStmt.setString(1, product);
        ResultSet rs = prepStmt.executeQuery();
        rs.next();
        System.out.println(rs.toString());
        quantity +=Integer.parseInt(rs.getString("Quantity_On_Hand"));
        prepStmt = dbConnection.prepareStatement("Update Products Set Quantity_On_Hand = ? Where Product_Name = ?");
        prepStmt.setString(1, String.valueOf(quantity));
        prepStmt.setString(2,product);
        prepStmt.executeUpdate();

    }
    public static String ticketCorrector(){
        String temp = String.valueOf(currentTicket);
        while (temp.length() < 3){
            temp = "0"+temp;
        }
        return temp;
    }
    // int Ticket, int restNum, String[] Meal_list
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
            if(orderList.contains("Hamburger")){
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
        System.out.println(currentTicket);
        currentTicket++;
        orderList = new ArrayList<>();
        System.out.println(currentTicket);
    }
    public static void readQueryData(String query) throws SQLException {
        myRs = Stmt.executeQuery(query);
        ResultSetMetaData rsmd = myRs.getMetaData();
        printData(myRs, rsmd);
    }
}