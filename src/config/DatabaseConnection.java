package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE = "libreria";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    public static Connection getConnectionForSetup() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE, USERNAME, PASSWORD);
    }
}

