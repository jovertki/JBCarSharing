package carsharing;

import javax.sql.DataSource;
import java.sql.*;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";


    static final String DB_PATH = "jdbc:h2:/Users/jovertki/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db";
    static final String DB_DEFAULT_NAME = "carsharingDefault";

    static String DB_URL = DB_PATH + "/" + DB_DEFAULT_NAME;

    public static void main(String[] args) {
        if ("-databaseFileName".equals(args[0]) && args.length >= 2) {
            DB_URL = DB_PATH + "/" + args[1];
        }

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
}
