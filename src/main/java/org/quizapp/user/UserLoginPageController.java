package org.quizapp.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginPageController {

    @FXML
    private Label loginStatus;
    @FXML
    private TextField usernameField, userPasswordField;
    @FXML
    private Button userLoginButton, goBackButton, registerButton;

    public static String loggedUser;

    @FXML
    private void onUserLogin() {

        String username = usernameField.getText();
        String password = userPasswordField.getText();

        if (!(username.isBlank()) || !(password.isBlank())) {
            if (validateCredentials()) {
                App.LOGGER.info("Provided user credentials are valid: " + username + " " + password);
                loggedUser = username;
                try {
                    App.setRoot("userMainPage");
                    App.LOGGER.info("Going to userMainPage.");
                } catch (IOException e) {
                    App.LOGGER.error("Error at loading userMainPage." + e.getMessage());
                }
            } else {
                loginStatus.setText("Invalid login or password. Please try again.");
                loginStatus.setTextFill(App.RED_COLOR);
                App.LOGGER.info("Provided user credentials are invalid: " + username + " " + password);
            }
            clearFields();
        } else {
            loginStatus.setText("Enter username and password!");
            loginStatus.setTextFill(App.RED_COLOR);
            App.LOGGER.info("Username or password not provided (or both).");
        }
    }

    private boolean validateCredentials() {
        DbHandler.connect();
        String sql = "SELECT count(1) FROM user WHERE username = '" + usernameField.getText()
                + "' AND password = '" + userPasswordField.getText() + "'";
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while (resultSet.next()) {
                if (resultSet.getInt(1) == 1) {
                    DbHandler.disconnect();
                    return true;
                }
            }
        } catch (SQLException e) {
            App.LOGGER.warn("User credentials validation error.");
        }
        DbHandler.disconnect();
        return false;
    }

    private void clearFields() {
        usernameField.clear();
        userPasswordField.clear();
    }

    @FXML
    private void goBack() {
        try {
            App.LOGGER.info("Going back from userLoginPage.");
            App.setRoot("mainPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going back from userLoginPage." + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegister() {
        try {
            App.LOGGER.info("Going to userRegisterPage.");
            App.setRoot("userRegisterPage");
        } catch (IOException e) {
            App.LOGGER.warn("Error at going to userRegisterPage." + e.getMessage());
            e.printStackTrace();
        }
    }
}
