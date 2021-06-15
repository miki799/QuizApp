package org.quizapp.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class UserMainPageController {

    @FXML
    Button logoutButton, startButton, resultButton;
    @FXML
    Label statusLabel;

    @FXML
    private void onStart() {

        DbHandler.connect();
        QuizPageController.questionAmount = returnLastId();
        if (QuizPageController.questionAmount < 2) {
            App.LOGGER.info("Not enough questions in database (at least 2).");
            statusLabel.setText("Not enough questions in database (at least 2).");
            statusLabel.setTextFill(App.RED_COLOR);
            DbHandler.disconnect();
            return;
        }
        DbHandler.disconnect();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your choice");
        alert.setHeaderText("Are you sure you want to start a Quiz?");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                App.LOGGER.info("Starting quiz. Going to quizPage.");
                App.setRoot("quizPage");
            } catch (IOException e) {
                App.LOGGER.error("Error at loading quizPage.");
                e.printStackTrace();
            }
        } else if (result.get() == ButtonType.CANCEL) {
            statusLabel.setText("Quiz canceled");
        }

    }

    @FXML
    private void onResult() {
        if (checkIfQuizResultIsValid()) {
            try {
                App.LOGGER.info("Opening resultPage pop up window.");
                Parent parent = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("userResultPage.fxml")));
                Scene scene = new Scene(parent);
                Stage popUpScreen = new Stage();
                popUpScreen.setTitle("Result");
                popUpScreen.getIcons().add(new Image("question-mark.png"));
                popUpScreen.setScene(scene);
                popUpScreen.initModality(Modality.APPLICATION_MODAL);
                popUpScreen.showAndWait();
            } catch (IOException e) {
                App.LOGGER.error("Error at loading resultPage pop up window." + e.getMessage());
            }
        } else {
            statusLabel.setText("You need to complete new Quiz first to see your result.");
            statusLabel.setTextFill(App.RED_COLOR);
        }
    }

    /*
     * Checks if User has done the latest quiz.
     */
    private boolean checkIfQuizResultIsValid() {
        DbHandler.connect();
        int userAmount = -1, quizAmount = 0;
        String sql = "SELECT questionAmount FROM user WHERE username='" + UserLoginPageController.loggedUser + "'";
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while (resultSet.next()) {
                userAmount = resultSet.getInt("questionAmount");
            }
        } catch (SQLException e) {
            DbHandler.disconnect();
            App.LOGGER.warn("Error at loading questionAmount from user table.");
        }
        quizAmount = returnLastId();
        App.LOGGER.info("Quiz amount: " + quizAmount);
        DbHandler.disconnect();
        if (quizAmount == userAmount && userAmount != 0) {
            App.LOGGER.info("User did complete the last quiz.");
            return true;
        } else {
            App.LOGGER.info("User didn't complete the last quiz.");
            return false;
        }
    }

    @FXML
    private void resultOn() {
        statusLabel.setText("Press to see your latest result.");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void resultOff() {
        statusLabel.setText("");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void quizOn() {
        statusLabel.setText("Press to start quiz.");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void quizOff() {
        if (statusLabel.getText().equals("Not enough questions in database (at least 2).")) return;
        statusLabel.setText("");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void logoutOn() {
        statusLabel.setText("Press to logout.");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void logoutOff() {
        statusLabel.setText("");
        statusLabel.setTextFill(App.GREEN_COLOR);
    }

    @FXML
    private void onLogout() {
        UserLoginPageController.loggedUser = "";
        try {
            App.LOGGER.info("Logging out from UserMainPage.");
            App.setRoot("userLoginPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at logging out from UserMainPage.");
            e.printStackTrace();
        }
    }

    /*
     * Returns last id from table
     */
    public static int returnLastId() {
        String sql = "SELECT questionId FROM questions ORDER BY questionId DESC LIMIT 1";
        int result = 0;
        try {
            ResultSet resultSet = DbHandler.executeQuery(sql);
            result = resultSet.getInt(1);
        } catch (SQLException e) {
            App.LOGGER.error("Error at retrieving last id from questions table. " + e.getMessage());
        }
        return result;
    }
}
