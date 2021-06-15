package org.quizapp.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.quizapp.App;
import org.quizapp.database.DbHandler;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddQuestionController {

    @FXML
    Button returnButton, addButton, cleanButton;

    @FXML
    Label infoLabel;

    @FXML
    TextField questionField, aField, bField, cField, dField, correctField;

    /*
     * Adds question to the table.
     */
    @FXML
    private void onAdd() {

        String question = questionField.getText();
        String optionA = aField.getText();
        String optionB = bField.getText();
        String optionC = cField.getText();
        String optionD = dField.getText();
        String correct = correctField.getText();

        DbHandler.connect();

        App.LOGGER.info("Adding question.");

        if (returnLastId() == App.MAX_NUMBER_OF_QUESTIONS) {
            infoLabel.setText("Maximum number of questions!");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.warn("Maximum number of questions. Can't add anything more.");
            DbHandler.disconnect();
            return;
        }

        if (ifAlreadyExists(question)) {
            infoLabel.setText("Question exists!");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.info("Question you want to add already exists.");
            DbHandler.disconnect();
            return;
        }

        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty()
                || optionC.isEmpty() || optionD.isEmpty() || correct.isEmpty()) {
            infoLabel.setText("Fields are empty!");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.warn("Question form fields are empty.");
            DbHandler.disconnect();
            return;
        } else if (question.equals(optionA) || question.equals(optionB)
                || question.equals(optionC) || question.equals(optionD) || question.equals(correct)) {
            infoLabel.setText("Question error.");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.warn("Text in one of the fields was the same like the question.");
            DbHandler.disconnect();
            return;
        } else if (optionA.equals(optionB) || optionA.equals(optionC) || optionA.equals(optionD) ||
                optionB.equals(optionC) || optionB.equals(optionD) || optionC.equals(optionD)) {
            infoLabel.setText("Duplicate answers.");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.warn("Duplicate answers.");
            DbHandler.disconnect();
            return;
        } else if (!optionA.equals(correct) && !optionB.equals(correct) && !optionC.equals(correct) && !optionD.equals(correct)) {
            infoLabel.setText("No correct answers.");
            infoLabel.setTextFill(App.RED_COLOR);
            App.LOGGER.warn("No correct answers provided.");
            DbHandler.disconnect();
            return;
        } else {
            if (addQuestion(question, optionA, optionB, optionC, optionD, correct) == 1) {
                App.LOGGER.info("Question has been added.");
                infoLabel.setText("Question has been added with success!");
                infoLabel.setTextFill(App.GREEN_COLOR);
                AdminQuestionsPageController.questionsLoaded = false;
            } else {
                App.LOGGER.warn("Question hasn't been added.");
                infoLabel.setText("Question not added!");
                infoLabel.setTextFill(App.RED_COLOR);
            }
            clear();
        }
        infoLabel.setText("Question added!");
        infoLabel.setTextFill(App.GREEN_COLOR);
        App.LOGGER.info("Question added!");
        DbHandler.disconnect();
    }

    /*
     * Adds question to the database.
     */
    private int addQuestion(String question, String optionA, String optionB, String optionC, String optionD, String correct) {
        String sql = "INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct) VALUES ('"
                + question + "', '" + optionA + "', '" + optionB + "', '" + optionC + "', '" + optionD + "', '" + correct + "')";
        int result = DbHandler.executeUpdate(sql);
        return result;
    }

    /*
     * Checks if question we want to add already exists.
     */
    private boolean ifAlreadyExists(String question) {
        App.LOGGER.info("Checking if question was already added: " + question);
        String sql = "SELECT count(1) FROM questions WHERE question = '" + question + "'";
        ResultSet resultSet = DbHandler.executeQuery(sql);
        try {
            while(resultSet.next()) {
                if (resultSet.getInt(1) == 1){
                    return true;
                }
            }
        } catch (SQLException e) {
            App.LOGGER.warn("Error at checking if question was already added.");
        }
        return false;
    }

    /*
     * Clears all fields in the form.
     */
    @FXML
    private void onClean() {
        clear();
        App.LOGGER.info("Question form fields cleaned.");
        infoLabel.setText("Fields cleaned!");
        infoLabel.setTextFill(App.GREEN_COLOR);
    }

    /*
     * Returns to the previous page.
     */
    @FXML
    private void onReturn() {
        App.LOGGER.info("Closing addQuestion pop up window.");
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

    /*
     * Returns last id from table
     */
    private int returnLastId() {
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

    private void clear() {
        questionField.clear();
        aField.clear();
        bField.clear();
        cField.clear();
        dField.clear();
        correctField.clear();
    }
}
