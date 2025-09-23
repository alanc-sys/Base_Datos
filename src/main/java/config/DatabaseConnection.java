package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

  private static final Properties properties = new Properties();
  private static final String DB_URL;
  private static final String DB_USER;
  private static final String DB_PASSWORD;

  static {
    try (InputStream input = DatabaseConnection.class.getClassLoader()
        .getResourceAsStream("application.properties")) {
      if (input == null) {
        System.out.println("Sorry, unable to find application.properties");
        throw new RuntimeException("Sorry, unable to find application.properties");
      }
      properties.load(input);
      DB_URL = properties.getProperty("db.url");
      DB_USER = properties.getProperty("db.user");
      DB_PASSWORD = properties.getProperty("db.password");
    } catch (IOException ex) {
      throw new RuntimeException("Error loading application.properties", ex);
    }
  }

  public static Connection getConnectionForSetup() throws SQLException {
    int lastSlash = DB_URL.lastIndexOf('/');
    String setupUrl = DB_URL.substring(0, lastSlash + 1);
    return DriverManager.getConnection(setupUrl, DB_USER, DB_PASSWORD);
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
  }
}
