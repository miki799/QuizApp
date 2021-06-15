package org.quizapp.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegisterPageController {

    @FXML
    TextField nameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField usernameField;
    @FXML
    TextField userPasswordField;
    @FXML
    Label registerStatus;
    @FXML
    Button userRegisterButton;
    @FXML
    Button goBackButton;

    @FXML
    private void onUserRegister() {
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String password = userPasswordField.getText();

        if (name.isBlank() || lastName.isBlank()
                || username.isBlank() || password.isBlank()) {
            App.LOGGER.info("Missing information in registration form.");
            registerStatus.setText("Enter every necessary information!");
            registerStatus.setTextFill(App.RED_COLOR);
            return;
        }

        if (!patternChecker(new String[]{name, lastName, username, password})) {
            App.LOGGER.info("Invalid input in registration form.");
            registerStatus.setText("Invalid input!");
            registerStatus.setTextFill(App.RED_COLOR);
            clearFields();
            return;
        }

        DbHandler.connect(); // connection with db

        if (ifAlreadyExists(username)) {
            App.LOGGER.info("Provided username is taken: " + username);
            registerStatus.setText("Provided username is taken!");
            registerStatus.setTextFill(App.RED_COLOR);
            clearFields();
            DbHandler.disconnect();
            return;
        }

        if (addUser(name, lastName, username, password) == 1) {
            App.LOGGER.info("New user has been added: " + username + " " + password);
            registerStatus.setText("User added with success!");
            registerStatus.setTextFill(App.GREEN_COLOR);
        } else {
            App.LOGGER.info("User has not been added: " + username + " " + password);
            registerStatus.setText("User not added!");
            registerStatus.setTextFill(App.RED_COLOR);
        }

        DbHandler.disconnect();
        clearFields();
    }

    private boolean patternChecker(String[] textInputs) {
        App.LOGGER.info("Checking registration fields (patternChecker).");
        String[] regex = {"[A-Z][a-z]*", "[A-Z][a-z]*", "[A-Za-z0-9]{5,}", "[A-Z][a-z0-9]{7,}"};
        boolean result = true;
        for (int i = 0; i < 4; i++) {
            Pattern pattern = Pattern.compile(regex[i]);
            Matcher matcher = pattern.matcher(textInputs[i]);
            if (!matcher.matches()) result = false;
        }
        return result;
    }

    private boolean ifAlreadyExists(String username) {
        App.LOGGER.info("Checking if user already exists: " + username);
        String sql = "SELECT count(1) FROM user WHERE username = '" + username + "'";
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while(resultSet.next()) {
                if (resultSet.getInt(1) == 1){
                    return true;
                }
            }
        } catch (SQLException e) {
            App.LOGGER.warn("Error at checking if user already exists.");
        }
        return false;
    }

    private int addUser(String name, String lastname, String username, String password) {
        String sql = "INSERT INTO user VALUES ('" + name + "', '" + lastname + "', '" + username + "', '" + password + "', '" + "0" + "', '" + "0" + "')";
        return DbHandler.executeUpdate(sql);
    }

    private void clearFields() {
        nameField.clear();
        lastNameField.clear();
        usernameField.clear();
        userPasswordField.clear();
    }

    @FXML
    private void showName() {
        registerStatus.setText("Should start with uppercase letter.");
        registerStatus.setTextFill(Color.web("0CCA4A")); // some green
    }

    @FXML
    private void showUsername() {
        registerStatus.setText("Minimum 5 characters: A-Z, a-z or 0-9.");
        registerStatus.setTextFill(Color.web("0CCA4A")); // some green
    }

    @FXML
    private void showPassword() {
        registerStatus.setText("Uppercase letter and min. 8 characters.");
        registerStatus.setTextFill(Color.web("0CCA4A")); // some green
    }

    @FXML
    private void goBack() {
        try {
            App.LOGGER.info("Going back from userRegisterPage.");
            App.setRoot("userLoginPage");
        } catch (IOException e) {
            App.LOGGER.info("Error at going back to userRegisterPage." + e.getMessage());
        }
    }

}
