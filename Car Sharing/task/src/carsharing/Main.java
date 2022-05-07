package carsharing;

import java.sql.*;
import java.util.*;


implement menu
have customer entity and dao
cant create
can?? list
cant rent


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
    }

    private void create() {
        String sql =  "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                "(ID INTEGER not NULL AUTO_INCREMENT, " +
                " NAME VARCHAR(255) UNIQUE NOT NULL, " +
                "RENTED_CAR_ID INTEGER NOT NULL," +
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

    }
}

class CarDAOImpl extends CarSharingDAO {

    public CarDAOImpl(String dbname) {
        setDbURL(dbname);
        create();
    }

    public List<Car> getAll() {
        String sql =  "Select * FROM CAR;";
        return getCars(sql);
    }

    public List<Car> getAllCompanyCars(Company company) {
        String sql =  "Select * FROM CAR WHERE COMPANY_ID = " + company.getId() + ";";
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
                out.add(new Car(rs.getInt("id"), rs.getString("name"), rs.getInt("company_id")));
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
    private String name;
    private int rentedCarId = 0;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Customer(String name) {
        this.name = name;
    }

    public Customer(String name, int carId) {

        this.name = name;
        this.rentedCarId = carId;
    }

    public Customer(int id, String name, int carId) {
        this.id = id;
        this.name = name;
        this.rentedCarId = carId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRentedCarId() {
        return rentedCarId;
    }

    public void setRentedCarId(int rentedCarId) {
        this.rentedCarId = rentedCarId;
    }
}

class Company {

    private int id;
    private String name;

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

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

class Car {
    private int id;
    private String name;
    private int companyId;

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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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
                case "1": listCompanies(); break;
                case "2": createCompany(); break;
                case "0": return;
            }
        }
    }

    private void createCompany() {
        System.out.println();
        System.out.println("Enter the company name: ");
        companies.add(new Company(input.nextLine()));

        System.out.println("The company was created!");
    }

    private void listCompanies() {
        List<Company> companies = this.companies.getAll();
        if (companies.size() == 0) {
            System.out.println("The company list is empty!");
            return;
        } else {
            System.out.println("Choose the company:");
            int i = 1;
            for (Company c : companies) {
                System.out.println(i + ". " + c.getName());
                i++;
            }
            System.out.println("0. Back");
        }
        int choice = Integer.parseInt(input.nextLine());
        if (choice > 0) {
            companyLoop(companies.get(choice - 1));
        }
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
//                case "3": createCustomer(); break;
                case "0": return;
            }
        }
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
//        int choice = Integer.parseInt(input.nextLine());
//        if (choice > 0) {
//            customerLoop(customers.get(choice - 1));
//        }
    }

//    private void customerLoop(Customer customer) {
//        System.out.println("'" + company.getName() + "' company");
//        while (true) {
//            System.out.println();
//            System.out.println("1. Car list\n" +
//                    "2. Create a car\n" +
//                    "0. Back");
//            switch (input.nextLine().toLowerCase()) {
//                case "1": listCars(company); break;
//                case "2": createCar(company); break;
//                case "0": return;
//            }
//        }
//    }
}


public class Main {
    public static void main(String[] args) {
        String dbname = "carsharingDefault";
        if ("-databaseFileName".equals(args[0]) && args.length >= 2) {
            dbname = args[1];
        }
        Menu menu = new Menu(dbname);
        menu.mainLoop();
    }
}
