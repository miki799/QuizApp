package org.quizapp.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserResultPageController implements Initializable {

    @FXML
    Button finishButton;

    @FXML
    Label finalResult, correctAnswers, deathSentence;

    private int correct;
    private int amount;
    private boolean done = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getData();

        if (done) {
            double result = ((double) correct/ (double) amount)*100;
            App.LOGGER.info("Correct answers: " + correct + " Question amount: " + amount);
            App.LOGGER.info("Final result: " + result + "%");
            finalResult.setText(finalResult.getText() + " " + result + "%");
            correctAnswers.setText(correctAnswers.getText() + " " + correct + "/" + amount);
            if (result >= 50.0) {
                deathSentence.setText("You have passed the quiz!");
                deathSentence.setTextFill(App.GREEN_COLOR);
            } else {
                deathSentence.setText("You didn't pass the quiz!");
                deathSentence.setTextFill(Color.BLACK);
            }
            done = false;
        }

    }

    /*
     * Gets result from database.
     */
    private void getData() {
        String sql = "SELECT result, questionAmount FROM user WHERE username='" + UserLoginPageController.loggedUser +"'";
        DbHandler.connect();
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while (resultSet.next()) {
                correct = resultSet.getInt("result");
                amount = resultSet.getInt("questionAmount");
            }

        } catch (SQLException e) {
            DbHandler.disconnect();
            App.LOGGER.warn("Error at loading user last results from database.");
        }
        App.LOGGER.info("User last result loaded from database.");
        done = true;
        DbHandler.disconnect();
    }

    @FXML
    private void onFinish() {
        App.LOGGER.info("Closing result pop up window.");
        Stage stage = (Stage) finishButton.getScene().getWindow();
        stage.close();
    }
}
