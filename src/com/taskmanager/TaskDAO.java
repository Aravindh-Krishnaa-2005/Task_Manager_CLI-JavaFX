package com.taskmanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public static void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, priority, deadline, status, completed) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getPriority());
            pstmt.setString(4, task.getDeadline());
            pstmt.setString(5, task.getStatus());
            pstmt.setBoolean(6, task.isCompleted());
            pstmt.setInt(7, Session.getInstance().getUserId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        rs.getBoolean("completed")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            // Reset IDs sequentially after delete
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET @count = 0");
                stmt.execute("UPDATE tasks SET id = @count:=@count+1");
                stmt.execute("ALTER TABLE tasks AUTO_INCREMENT = 1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markCompleted(int id) {
        String sql = "UPDATE tasks SET completed = TRUE, status='Completed' WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
