package com;


import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.sql.Statement;

@WebService(serviceName = "Service")
public class Service {

    /**
     * Sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + "!";
    }

    /**
     * Login operation for taskmanager schema
     */
    @WebMethod(operationName = "login")
public int login(@WebParam(name = "login") String login, @WebParam(name = "pwd") String pwd) {
    String query = "SELECT idusers, pwd FROM users WHERE login = ?";
    int userId = -1; // Default value for invalid credentials

    try {
        // Load MySQL JDBC driver
        Class.forName("com.mysql.jdbc.Driver");

        // Connect to the taskmanager database
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/taskmanager?useSSL=false", "root", "admin");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, login);

            // Execute the query and compare the password
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("pwd");

                    // Compare using BCrypt
                    if (BCrypt.checkpw(pwd, storedPassword)) {
                        userId = resultSet.getInt("idusers"); // Retrieve user ID
                    }
                }
            }
        }
    } catch (ClassNotFoundException e) {
        System.out.println("JDBC Driver not found: " + e.getMessage());
    } catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
    }
    return userId;
}

    /**
     * Signup operation for taskmanager schema
     */
    @WebMethod(operationName = "signup")
    public boolean signup(@WebParam(name = "name") String name, @WebParam(name = "login") String login, @WebParam(name = "pwd") String pwd) {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE login = ?";
        String insertQuery = "INSERT INTO users (name, login, pwd) VALUES (?, ?, ?)";
        boolean success = false;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/taskmanager?useSSL=false", "root", "admin");
                 PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                 PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

                // Check if login already exists
                checkStmt.setString(1, login);
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        return false; // Login already exists
                    }
                }

                // Hash the password
                String hashedPassword = BCrypt.hashpw(pwd, BCrypt.gensalt());

                // Insert user into the database
                insertStmt.setString(1, name);
                insertStmt.setString(2, login);
                insertStmt.setString(3, hashedPassword);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    success = true; // Signup successful
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return success;
    }
}
