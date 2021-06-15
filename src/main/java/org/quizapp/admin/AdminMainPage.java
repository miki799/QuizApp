package org.quizapp.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.quizapp.App;

import java.io.IOException;

public class AdminMainPage {

    @FXML
    Button logoutButton, resultsButton, questionsButton;
    @FXML
    Label infoLabel;

    @FXML
    private void onResults() {
        try {
            App.LOGGER.info("Going to adminResultsPage.");
            App.setRoot("adminResultsPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going to adminResultsPage.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onQuestions() {
        try {
            App.LOGGER.info("Going to adminQuestionsPage.");
            App.setRoot("adminQuestionsPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going to adminQuestionsPage.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        AdminLoginPageController.loggedAdmin = "";
        try {
            App.LOGGER.info("Logging out from AdminMainPage.");
            App.setRoot("adminLoginPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at logging out from AdminMainPage.");
            e.printStackTrace();
        }
    }

    @FXML
    private void resultOn() {
        infoLabel.setText("Press to see results.");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void resultOff() {
        infoLabel.setText("");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void questionsOn() {
        infoLabel.setText("Press to add/edit questions.");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void questionsOff() {
        infoLabel.setText("");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void logoutOn() {
        infoLabel.setText("Press to logout.");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void logoutOff() {
        infoLabel.setText("");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

}
