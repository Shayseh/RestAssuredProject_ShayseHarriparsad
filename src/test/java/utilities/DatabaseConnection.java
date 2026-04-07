package utilities;

import org.testng.annotations.Test;

import java.sql.*;

public class DatabaseConnection {

    public static String getEmail;
    public static String getPassword;

    public static void connectToDatabase() throws SQLException {
        // Code to establish a connection to the database

        String databaseURL = "jdbc:mysql://102.222.124.22:3306/ndosian6b8b7_teaching";
        String databaseUser = "ndosian6b8b7_teaching";
        String databasePassword = "^{SF0a=#~[~p)@l1";

        // Establishing a connection to the database using try-with-resources to ensure proper resource management
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUser, databasePassword)) {

            // Code to execute a query and retrieve data from the database
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM loginUser WHERE id = 5")) {

                while (resultSet.next()) {
                    getEmail = resultSet.getString("email");
                    getPassword = resultSet.getString("password");
                    System.out.println("Email: " + getEmail + ", Password: " + getPassword);
                }
            } catch (SQLException e) {
                System.out.println("Database connection failed: " + e.getMessage());
            }
        }
    }
}
