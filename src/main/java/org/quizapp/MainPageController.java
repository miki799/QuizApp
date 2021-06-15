package org.quizapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainPageController {

    @FXML
    Button simpleUserButton;
    @FXML
    Button powerfulAdminButton;

    @FXML
    private void goToUserLoginPage() {
        try {
            App.LOGGER.info("Going to userLoginPage.");
            App.setRoot("userLoginPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going to userLoginPage.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAdminLoginPage() {
        try {
            App.LOGGER.info("Going to adminLoginPage.");
            App.setRoot("adminLoginPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going to adminLoginPage.");
            e.printStackTrace();
        }
    }

}
