package com.taskmanager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class AuthController {

    // Login fields
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Button loginButton;
    @FXML private Hyperlink goToRegister;

    // Register fields (same controller used for both fxmls; unused fields will be null)
    @FXML private TextField regUsername;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regPasswordConfirm;
    @FXML private Button registerButton;
    @FXML private Hyperlink goToLogin;

    private Stage stage;
    private Scene scene;
    private Parent root;

    // ------ Login action ------
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please enter username and password.");
            return;
        }

        User user = UserDAO.authenticate(username, password);
        if (user != null) {
            Session.getInstance().setUserId(user.getId());
            Session.getInstance().setUsername(user.getUsername());
            // load TaskView
            loadTaskView(event);
        } else {
            showAlert("Invalid credentials. Try again.");
        }
    }

    // ------ Go to register view ------
    @FXML
    private void showRegisterView(ActionEvent event) {
        navigateTo("/com.taskmanager/RegisterView.fxml", event);
    }

    // ------ Register action ------
    @FXML
    private void handleRegister(ActionEvent event) {
        String username = regUsername.getText().trim();
        String password = regPassword.getText();
        String confirm = regPasswordConfirm.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill all fields.");
            return;
        }
        if (!password.equals(confirm)) {
            showAlert("Passwords do not match.");
            return;
        }

        User u = new User(username, password);
        boolean ok = UserDAO.register(u);
        if (ok) {
            showAlert("Registration successful. Please login.");
            // navigate to login
            navigateTo("/com.taskmanager/LoginView.fxml", event);
        } else {
            showAlert("Username already exists. Choose another.");
        }
    }

    // ------ Go to login view ------
    @FXML
    private void showLoginView(ActionEvent event) {
        navigateTo("/com.taskmanager/LoginView.fxml", event);
    }

    // helper: load TaskView after successful login
    private void loadTaskView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.taskmanager/TaskView.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Task Manager - " + Session.getInstance().getUsername());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // generic navigation helper (to a fxml path)
    private void navigateTo(String fxmlResource, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResource));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }


    /*@FXML
    public void onLogin(ActionEvent event) throws IOException {
        // Load the dashboard view
        root = FXMLLoader.load(getClass().getResource("/com.taskmanager/TaskView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Task Dashboard");
        stage.show();
    }

    @FXML
    public void onRegister(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/com.taskmanager/RegisterView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
     */
}
