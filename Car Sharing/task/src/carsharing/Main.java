package carsharing;

import java.sql.*;
import java.util.*;


interface CompanyDAO {
    List<Company> getAllCompanies();
    void addCompany(Company company);
}

class CompanyDAOImpl implements CompanyDAO {


    private final String JDBC_DRIVER = "org.h2.Driver";

    private final String DB_PATH = "jdbc:h2:/Users/jovertki/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db";
    private String DB_NAME = "carsharingDefault";

    private String DB_URL;


    public void setDbUrl(String dbname) {
        DB_NAME = dbname;
        DB_URL = DB_PATH + "/" + DB_NAME;
    }

    @Override
    public List<Company> getAllCompanies() {
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

    @Override
    public void addCompany(Company company) {
        String sql =  "INSERT INTO COMPANY(name) " +
                        "VALUES ('" + company.getName() + "');";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.executeUpdate(sql);
        } catch(Exception se) {
            //Handle errors for JDBC
            se.printStackTrace();
        }//Handle errors for Class.forName

    }

    public void updateConstraints() {
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

    public void create() {
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

/*
class Database {
    private final String JDBC_DRIVER = "org.h2.Driver";

    private final String DB_PATH = "jdbc:h2:/Users/jovertki/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db";
    private String DB_NAME = "carsharingDefault";

    private String DB_URL = DB_PATH + "/" + DB_NAME;


    public void setDbUrl(String dbname) {
        DB_NAME = dbname;
        DB_URL = DB_PATH + "/" + DB_NAME;
    }

    public void createDB() {

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);//?????
            //STEP 2: Open a connection
            conn.setAutoCommit(true);
            //STEP 3: Execute a query
            String sql =  "CREATE TABLE COMPANY " +
                    "(ID INTEGER not NULL, " +
                    " NAME VARCHAR(255), " +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(sql);
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    public void updateConstraints() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            String sql =  "ALTER TABLE COMPANY " +
                    "ALTER COLUMN id INTEGER NOT NULL AUTO_INCREMENT=0;" +
                    "ALTER TABLE COMPANY " +
                    "ALTER COLUMN NAME VARCHAR(255) NOT NULL;" +
                    "ALTER TABLE COMPANY " +
                    "ADD UNIQUE (NAME);";
            stmt.executeUpdate(sql);
        } catch(SQLException se) {
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}
*/

class Menu {

    private CompanyDAOImpl table;


    static public final Scanner input = new Scanner(System.in);

    Menu(CompanyDAOImpl table) {
        this.table = table;
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
        table.addCompany(new Company(input.nextLine()));

        System.out.println("The company was created!");
    }

    private void listCompanies() {
        List<Company> companies = table.getAllCompanies();
        if (companies.size() == 0) {
            System.out.println("The company list is empty!");
        } else {
            System.out.println("Company list:");
            companies.stream()
                    .sorted(Comparator.comparingInt(Company::getId))
                    .forEach(c -> System.out.println(c.getId() + ". " + c.getName()));
        }

    }

    public void mainLoop(){
        while (true) {
            System.out.println("1. Log in as a manager\n" +
                    "0. Exit");
            switch (input.nextLine().toLowerCase()) {
                case "1": managerLoop(); break;
                case "0": return;
            }
        }
    }
}


public class Main {
    public static void main(String[] args) {
        CompanyDAOImpl table = new CompanyDAOImpl();
        if ("-databaseFileName".equals(args[0]) && args.length >= 2) {
            table.setDbUrl(args[1]);
        }
        table.create();
        table.updateConstraints();

        Menu menu = new Menu(table);
        menu.mainLoop();


    }
}
