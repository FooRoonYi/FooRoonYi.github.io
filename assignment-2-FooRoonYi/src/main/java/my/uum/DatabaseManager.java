package my.uum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is for managing the SQLite database.
 *
 * @author  Foo Roon Yi
 */
public class DatabaseManager {
    /**
     * This method is for connecting to a database.
     */
    public static void DatabaseManager() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:asg2_database.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * This method is the main method to manage the database connection.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DatabaseManager();
    }
}