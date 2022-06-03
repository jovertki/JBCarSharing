package carsharing;

import java.sql.*;
import java.util.*;


//implement menu
//have customer entity and dao
//cant create
//can?? list
//cant rent


abstract class CarSharingDAO {
    public static String JDBC_DRIVER = "org.h2.Driver";
    public static final String DB_PATH = "jdbc:h2:/Users/jovertki/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db";
    public static String DB_NAME = "carsharingDefault";
    protected String DB_URL;
    public void setDbURL(String dbName) {
        DB_NAME = dbName;
        DB_URL = DB_PATH + "/" + DB_NAME;
    }
}

class CustomerDAOImpl extends CarSharingDAO {


    public CustomerDAOImpl(String dbname) {
        setDbURL(dbname);
        create();
        updateConstraints();
    }

    private void create() {
        String sql =  "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                "(ID INTEGER not NULL AUTO_INCREMENT, " +
                " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                "RENTED_CAR_ID INTEGER," +
                " PRIMARY KEY ( id )," +
                "CONSTRAINT fk_rented_car FOREIGN KEY (RENTED_CAR_ID) " +
                "REFERENCES CAR( id ))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            Class.forName(JDBC_DRIVER);
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            se.printStackTrace();
        }

    }

    private void updateConstraints() {
        String sql =  "ALTER TABLE CUSTOMER\n" +
                "ALTER COLUMN RENTED_CAR_ID INT NULL;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    public List<Customer> getAll() {

        String sql =  "Select * FROM CUSTOMER";
        List<Customer> out = new LinkedList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            ResultSet rs = stmt.executeQuery(sql);
            // STEP 4: Extract data from result set
            while(rs.next()) {
                out.add(new Customer(rs.getInt("id"), rs.getString("name"), rs.getInt("rented_car_id")));
            }
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        return out;
    }

    public void add(Customer entity) {
        String sql =  "INSERT INTO Customer(name) " +
                "VALUES ('" + entity.getName() + "');";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    public void returnCar(Customer customer) {
        String sql =  "update CUSTOMER\n" +
                "set RENTED_CAR_ID = NULL\n" +
                "where id =" + customer.getId() + ";";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            Class.forName(JDBC_DRIVER);
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            se.printStackTrace();
        }
    }
}

class CarDAOImpl extends CarSharingDAO {

    public CarDAOImpl(String dbname) {
        setDbURL(dbname);
        create();
    }

    public List<Car> getAllCompanyCars(Company company) {
        String sql =  "Select * FROM CAR WHERE COMPANY_ID = " + company.getId() + ";";
        return getCars(sql);
    }

    public List<Car> getAllAvailableCompanyCars(Company company) {
        String sql =  "Select distinct car.ID, car.NAME, car.company_id\n" +
                "FROM CAR\n" +
                "LEFT JOIN CUSTOMER on CAR.ID = CUSTOMER.RENTED_CAR_ID\n" +
                "WHERE CAR.COMPANY_ID = " + company.getId() + " AND CUSTOMER.ID IS NULL;";
        return getCars(sql);
    }

    private List<Car> getCars(String sql) {
        List<Car> out = new LinkedList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            ResultSet rs = stmt.executeQuery(sql);
            // STEP 4: Extract data from result set
            while(rs.next()) {
                out.add(new Car(rs.getInt("id"), rs.getString("name"), rs.getInt("COMPANY_ID")));
            }
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        return out;
    }

    public void add(Car entity) {
        String sql =  "INSERT INTO CAR(name, COMPANY_ID) " +
                "VALUES ('" + entity.getName() + "', " + entity.getCompanyId() +");";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    private void create() {
        String sql =  "CREATE TABLE IF NOT EXISTS CAR " +
                "(ID INTEGER not NULL AUTO_INCREMENT, " +
                " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                "COMPANY_ID INTEGER NOT NULL," +
                " PRIMARY KEY ( id )," +
                "CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID) " +
                "REFERENCES COMPANY( id ))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);//?????
            //STEP 2: Open a connection
            conn.setAutoCommit(true);
            //STEP 3: Execute a query

            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    public void rent(Car car, Customer customer) {
        String sql =  "update CUSTOMER\n" +
                "set RENTED_CAR_ID = " + car.getId() + "\n" +
                "where ID = " + customer.getId() + ";";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            Class.forName(JDBC_DRIVER);
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            se.printStackTrace();
        }
    }

    public String[] getCustomerCarData(Customer customer) {
        String sql = "SELECT C2.name as company_name, C.name\n as car_name " +
                "FROM customer\n" +
                "join CAR C on CUSTOMER.RENTED_CAR_ID = C.ID\n" +
                "join COMPANY C2 on C.COMPANY_ID = C2.ID\n" +
                "where customer.id = "+ customer.getId() + ";";
        String[] out = new String[2];
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            ResultSet rs = stmt.executeQuery(sql);
            // STEP 4: Extract data from result set
            while(rs.next()) {
                out[0] = rs.getString("company_name");
                out[1] = rs.getString("car_name");
            }
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        return out;
    }
}

class CompanyDAOImpl extends CarSharingDAO {

    public CompanyDAOImpl(String dbname) {
        setDbURL(dbname);
        create();
        updateConstraints();
    }

    public List<Company> getAll() {
        String sql =  "Select * FROM COMPANY";
        List<Company> out = new LinkedList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            ResultSet rs = stmt.executeQuery(sql);
            // STEP 4: Extract data from result set
            while(rs.next()) {
                out.add(new Company(rs.getInt("id"), rs.getString("name")));
            }
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName
        return out;
    }

    public void add(Company entity) {
        String sql =  "INSERT INTO COMPANY(name) " +
                        "VALUES ('" + entity.getName() + "');";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    private void updateConstraints() {
        String sql =  "ALTER TABLE COMPANY " +
                "ALTER COLUMN id INTEGER NOT NULL AUTO_INCREMENT;" +
                "ALTER TABLE COMPANY " +
                "ALTER COLUMN NAME VARCHAR(255) NOT NULL;" +
                "ALTER TABLE COMPANY " +
                "ADD UNIQUE (NAME);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    private void create() {
        String sql =  "CREATE TABLE IF NOT EXISTS COMPANY " +
                "(ID INTEGER not NULL, " +
                " NAME VARCHAR(255), " +
                " PRIMARY KEY ( id ))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);//?????
            //STEP 2: Open a connection
            conn.setAutoCommit(true);
            //STEP 3: Execute a query

            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }
}

class Customer {
    private int id = 0;
    private final String name;
    private int rentedCarId = 0;


    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id, String name, int carId) {
        this.id = id;
        this.name = name;
        this.rentedCarId = carId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(int rentedCarId) {
        this.rentedCarId = rentedCarId;
    }
}

class Company {

    private final int id;
    private final String name;

    public Company(String name) {
        this.id = 0;
        this.name = name;
    }

    public Company(int id, String name ) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public int getId() {
        return id;
    }

}

class Car {
    private final int id;
    private final String name;
    private final int companyId;

    public Car(String name, int companyId) {
        this.id = 0;
        this.name = name;
        this.companyId = companyId;
    }

    public Car(int id, String name, int companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public int getCompanyId() {
        return companyId;
    }

}

class Menu {

    private final CompanyDAOImpl companies;
    private final CarDAOImpl cars;
    private final CustomerDAOImpl customers;

    static public final Scanner input = new Scanner(System.in);

    Menu(String dbname) {
        this.companies = new CompanyDAOImpl(dbname);
        this.cars = new CarDAOImpl(dbname);
        this.customers = new CustomerDAOImpl(dbname);
    }

    private void companyLoop(Company company){
        System.out.println("'" + company.getName() + "' company");
        while (true) {
            System.out.println();
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");
            switch (input.nextLine().toLowerCase()) {
                case "1": listCars(company); break;
                case "2": createCar(company); break;
                case "0": return;
            }
        }
    }

    private void createCar(Company company) {
        System.out.println();
        System.out.println("Enter the car name: ");
        cars.add(new Car(input.nextLine(), company.getId()));
        System.out.println("The car was added!");

    }

    private void listCars(Company company) {
        List<Car> cars = this.cars.getAllCompanyCars(company);
        if (cars.size() == 0) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("Car list:");
            int i = 1;
            for (Car c : cars) {
                System.out.println(i + ". " + c.getName());
                i++;
            }
            System.out.println("0. Back");
        }
    }

    private void managerLoop(){
        while (true) {
            System.out.println();
            System.out.println("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back");
            switch (input.nextLine().toLowerCase()) {
                case "1":
                    startCompanyLoop(listCompanies());
                break;
                case "2": createCompany(); break;
                case "0": return;
            }
        }
    }

    private void startCompanyLoop(List<Company> companies){
        if (companies.size() > 0) {
            int choice = Integer.parseInt(input.nextLine());
            if (choice > 0) {
                companyLoop(companies.get(choice - 1));
            }
        }
    }

    private void createCompany() {
        System.out.println();
        System.out.println("Enter the company name: ");
        companies.add(new Company(input.nextLine()));

        System.out.println("The company was created!");
    }

    private List<Company> listCompanies() {
        List<Company> companies = this.companies.getAll();
        if (companies.size() == 0) {
            System.out.println("The company list is empty!");
            return companies;
        } else {
            System.out.println("Choose the company:");
            int i = 1;
            for (Company c : companies) {
                System.out.println(i + ". " + c.getName());
                i++;
            }
            System.out.println("0. Back");
        }
        return companies;
    }

    public void mainLoop(){
        while (true) {
            System.out.println("1. Log in as a manager\n" +
                    "2. Log in as a customer\n" +
                    "3. Create a customer\n" +
                    "0. Exit");
            switch (input.nextLine().toLowerCase()) {
                case "1": managerLoop(); break;
                case "2": loginLoop(); break;
                case "3": createCustomer(); break;
                case "0": return;
            }
        }
    }

    private void createCustomer() {
        System.out.println();
        System.out.println("Enter the customer name: ");
        customers.add(new Customer(input.nextLine()));
        System.out.println("The customer was added!");
    }

    private void loginLoop() {
        List<Customer> customers = this.customers.getAll();
        if (customers.size() == 0) {
            System.out.println("The customer list is empty!");
            return;
        } else {
            System.out.println("Customer list:");
            int i = 1;
            for (Customer c : customers) {
                System.out.println(i + ". " + c.getName());
                i++;
            }
            System.out.println("0. Back");
        }
        int choice = Integer.parseInt(input.nextLine());
        if (choice > 0) {
            customerLoop(customers.get(choice - 1));
        }
    }

    private void customerLoop (Customer customer) {
        while (true) {
            System.out.println();
            System.out.println("1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");
            switch (input.nextLine().toLowerCase()) {
                case "1": rentCar(customer); break;
                case "2": returnCar(customer); break;
                case "3": showRentedCar(customer); break;
                case "0": return;
            }
        }
    }

    private void showRentedCar(Customer customer) {
        if (customer.getRentedCarId() == 0) {
            System.out.println("You didn't rent a car!");
            return;
        }
        String[] data = cars.getCustomerCarData(customer);
        System.out.println("Your rented car: ");
        System.out.println(data[1]);
        System.out.println("Company: ");
        System.out.println(data[0]);
    }

    private void returnCar(Customer customer) {
        if (customer.getRentedCarId() == 0) {
            System.out.println("You didn't rent a car!");
            return;
        }
        customers.returnCar(customer);
        customer.setRentedCarId(0);
        System.out.println("You've returned a rented car!");
    }

    private void chooseCarLoop(Company company, Customer customer) {
        List<Car> cars = this.cars.getAllAvailableCompanyCars(company);
        if (cars.size() == 0) {
            System.out.println("The cars list is empty!");
            return;
        } else {
            System.out.println("Choose the car:");
            int i = 1;
            for (Car c : cars) {
                System.out.println(i + ". " + c.getName());
                i++;
            }
            System.out.println("0. Back");
        }
        int choice = Integer.parseInt(input.nextLine());
        if (choice > 0) {
            Car rented = cars.get(choice - 1);
            this.cars.rent(rented, customer);
            customer.setRentedCarId(rented.getId());
            System.out.println("\nYou rented '" + rented.getName() + "'");
        }
    }

    private void rentCar(Customer customer) {
        if (customer.getRentedCarId() != 0) {
            System.out.println("You've already rented a car!");
            return;
        }
        List<Company> companies = listCompanies();
        if (companies.size() == 0) {
            return;
        }
        int choice = Integer.parseInt(input.nextLine());
        if (choice > 0) {
            chooseCarLoop(companies.get(choice - 1), customer);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        String dbname = "carsharingDefault";
        if (args.length > 0 && "-databaseFileName".equals(args[0]) && args.length >= 2) {
            dbname = args[1];
        }
        Menu menu = new Menu(dbname);
        menu.mainLoop();
    }
}
