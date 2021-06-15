package org.quizapp.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginPageController {

    @FXML
    private Label loginStatus;
    @FXML
    private TextField adminNameField;
    @FXML
    private TextField adminPasswordField;
    @FXML
    private Button adminLoginButton;
    @FXML
    private Button goBackButton;

    public static String loggedAdmin;

    @FXML
    private void onAdminLogin() {
        if (!(adminNameField.getText().isBlank()) || !(adminPasswordField.getText().isBlank())) {
            if (validateCredentials()) {
                //loginStatus.setText("OK!");
                //loginStatus.setTextFill(App.GREEN_COLOR);
                loggedAdmin = adminNameField.getText();
                App.LOGGER.info("Provided admin credentials are valid: " + adminNameField.getText() + " " + adminPasswordField.getText());
                try {
                    App.LOGGER.info("Going to AdminMainPage.");
                    App.setRoot("AdminMainPage");
                } catch (IOException e) {
                    App.LOGGER.error("Error at going to AdminMainPage." + e.getMessage());
                }
            } else {
                loginStatus.setText("Invalid login or password. Please try again.");
                loginStatus.setTextFill(App.RED_COLOR);
                App.LOGGER.info("Provided admin credentials are invalid: " + adminNameField.getText() + " " + adminPasswordField.getText());
            }
            clearFields();
        } else {
            loginStatus.setText("Enter admin name and password!");
            loginStatus.setTextFill(App.RED_COLOR); // Red Crayola
            App.LOGGER.info("Admin name or password not provided (or both).");
        }
    }

    private boolean validateCredentials() {
        DbHandler.connect();
        String sql = "SELECT count(1) FROM admin WHERE adminName = '" + adminNameField.getText()
                + "' AND adminPassword = '" + adminPasswordField.getText() + "'";
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while(resultSet.next()) {
                if (resultSet.getInt(1) == 1){
                    DbHandler.disconnect();
                    return true;
                }
            }
        } catch (SQLException e) {
            App.LOGGER.warn("Admin credentials validation error.");
        }
        DbHandler.disconnect();
        return false;
    }

    @FXML
    private void goBack() {
        try {
            App.LOGGER.info("Going back to mainPage.");
            App.setRoot("mainPage");
        } catch (IOException e) {
            App.LOGGER.warn("Error at going back to mainPage." + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        adminNameField.clear();
        adminPasswordField.clear();
    }

}
