package com.taskmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class TaskController {

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> idColumn;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> descriptionColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, String> deadlineColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> completedColumn;

    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField priorityField;
    @FXML private TextField deadlineField;
    @FXML private ChoiceBox<String> statusChoice;
    @FXML private CheckBox completedCheckBox;

    private ObservableList<Task> taskList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Table column bindings
        idColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getId())));
        titleColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getDescription()));
        priorityColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getPriority()));
        deadlineColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getDeadline()));
        statusColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        completedColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().isCompleted() ? "Yes" : "No"));

        statusChoice.setItems(FXCollections.observableArrayList("Pending", "In Progress", "Completed"));
        statusChoice.setValue("Pending");

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        String query = "SELECT * FROM tasks ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                taskList.add(new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("priority"),
                        rs.getString("deadline"),
                        rs.getString("status"),
                        rs.getBoolean("completed")
                ));
            }
            taskTable.setItems(taskList);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void onAddTask() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String priority = priorityField.getText().trim();
        String deadline = deadlineField.getText().trim();
        String status = statusChoice.getValue();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Task title cannot be empty!");
            return;
        }

        String sql = "INSERT INTO tasks (title, description, priority, deadline, status, completed) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(conn).prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, priority);
            pstmt.setString(4, deadline);
            pstmt.setString(5, status);
            pstmt.setBoolean(6, completed);
            pstmt.executeUpdate();

            loadTasks();
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void onDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a task to delete.");
            return;
        }

        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = Objects.requireNonNull(conn).prepareStatement(sql)) {

            pstmt.setInt(1, selectedTask.getId());
            pstmt.executeUpdate();
            reorderIDs(conn);
            loadTasks();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    private void reorderIDs(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET @count = 0");
            stmt.execute("UPDATE tasks SET id = (@count:=@count+1) ORDER BY id");
        }
    }

    @FXML
    public void onRefresh() {
        loadTasks();
    }

    @FXML
    public void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.taskmanager/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load login view.");
        }
    }

    private void clearFields() {
        titleField.clear();
        descriptionField.clear();
        priorityField.clear();
        deadlineField.clear();
        completedCheckBox.setSelected(false);
        statusChoice.setValue("Pending");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onLogout() {
        try {
            // Clear the current user session
            Session.getInstance().clear();  // this resets username & userId

            // Load LoginView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.taskmanager/LoginView.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) taskTable.getScene().getWindow();

            // Set the login scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Task Manager - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
