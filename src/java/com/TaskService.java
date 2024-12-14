package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

@WebService(serviceName = "TaskService")
public class TaskService {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/taskmanager?useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "admin";

    // Load the JDBC Driver
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Create a new task for a specific user
     */
    @WebMethod(operationName = "createTask")
    public boolean createTask(@WebParam(name = "title") String title, 
                              @WebParam(name = "status") String status, 
                              @WebParam(name = "due_date") String dueDate,
                              @WebParam(name = "hour") String hour,
                              @WebParam(name = "idusers") int idusers) {
        String query = "INSERT INTO tasks (title, status, due_date, hour, date_created, idusers) VALUES (?, ?, ?, ?, CURDATE(), ?)";
        boolean success = false;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, status);
            preparedStatement.setDate(3, Date.valueOf(dueDate));
            preparedStatement.setTime(4, Time.valueOf(hour));
            preparedStatement.setInt(5, idusers);

            int rows = preparedStatement.executeUpdate();
            success = rows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return success;
    }

    /**
     * Retrieve tasks for a specific user
     */
    @WebMethod(operationName = "getUserTasks")
    public List<String> getUserTasks(@WebParam(name = "idusers") int idusers) {
        List<String> tasks = new ArrayList<>();
        String query = "SELECT idtask, title, status, hour, due_date, date_created FROM tasks WHERE idusers = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idusers);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("idtask");
                    String title = resultSet.getString("title");
                    String status = resultSet.getString("status");
                    String hour = resultSet.getTime("hour").toString();
                    String dueDate = resultSet.getDate("due_date").toString();
                    String dateCreated = resultSet.getDate("date_created").toString();

                    String task = "ID: " + id + ", Title: " + title + ", Status: " + status 
                                  + ", Due Date: " + dueDate + " " + hour + ", Date Created: " + dateCreated;
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Update a task for a specific user
     */
    @WebMethod(operationName = "updateUserTask")
    public boolean updateUserTask(@WebParam(name = "idtask") int idtask, 
                                  @WebParam(name = "title") String title, 
                                  @WebParam(name = "status") String status, 
                                  @WebParam(name = "due_date") String dueDate,
                                  @WebParam(name = "hour") String hour,
                                  @WebParam(name = "idusers") int idusers) {
        String query = "UPDATE tasks SET title = ?, status = ?, due_date = ?, hour = ? WHERE idtask = ? AND idusers = ?";
        boolean success = false;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, status);
            preparedStatement.setDate(3, Date.valueOf(dueDate));
            preparedStatement.setTime(4, Time.valueOf(hour));
            preparedStatement.setInt(5, idtask);
            preparedStatement.setInt(6, idusers);

            int rows = preparedStatement.executeUpdate();
            success = rows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return success;
    }

    /**
     * Delete a task for a specific user
     */
    @WebMethod(operationName = "deleteUserTask")
    public boolean deleteUserTask(@WebParam(name = "idtask") int idtask, 
                                  @WebParam(name = "idusers") int idusers) {
        String query = "DELETE FROM tasks WHERE idtask = ? AND idusers = ?";
        boolean success = false;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idtask);
            preparedStatement.setInt(2, idusers);

            int rows = preparedStatement.executeUpdate();
            success = rows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return success;
    }
}
