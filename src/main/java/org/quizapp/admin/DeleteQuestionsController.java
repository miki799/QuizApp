package org.quizapp.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.quizapp.App;
import org.quizapp.Question;
import org.quizapp.database.DbHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public class DeleteQuestionsController {

    @FXML
    Button returnButton, deleteButton;
    @FXML
    TextField input;
    @FXML
    Label status;

    @FXML
    private void onDelete() {

        String[] idToDelete = (input.getText()).split(" +"); //

        if (!checkNumeric(idToDelete)) {
            status.setText("No numeric values!");
            status.setTextFill(App.RED_COLOR);
            App.LOGGER.info("Wrong values were provided");
            return;
        }

        if (!checkSizeAndSign(idToDelete)) {
            status.setText("Too high or negative values!");
            status.setTextFill(App.RED_COLOR);
            status.setText("Too high or negative values!");
            return;
        }

        int[] ids = new int[idToDelete.length];
        for (int i = 0; i < idToDelete.length; i++) {
            ids[i] = Integer.parseInt(idToDelete[i]);
            App.LOGGER.info("ids to delete = " + ids[i]);
        }

        deleting(ids);

    }

    /*
     * Deletes specified questions from database.
     */
    private void deleting(int[] array) {
        Map<Integer, Question> questionMap = new TreeMap<>();

        // Putting Question to a TreeMap
        for (Question q : AdminQuestionsPageController.questions) {
            questionMap.put(q.getQuestionId(), q);
        }

        // Removing specified questions
        for (int id : array) {
            questionMap.remove(id);
        }

        // LOGGING
        //for (Map.Entry<Integer, Question> entry : questionMap.entrySet()) {
        //    App.LOGGER.info("QuestionId: " + entry.getValue().getQuestionId());
        //}

        DbHandler.connect();
        if (DbHandler.deleteTable("questions") == 0) {
            App.LOGGER.info("Questions deleted.");
            status.setText("Questions deleted.");
            status.setTextFill(App.GREEN_COLOR);
            if (DbHandler.createTable("questions") == 0) {
                App.LOGGER.info("New questions table created.");
            } else {
                DbHandler.disconnect();
                App.LOGGER.info("Error at creating new table");
                return;
            }
            int result = 0;
            // Inserting questions back to database
            for (Map.Entry<Integer, Question> entry : questionMap.entrySet()) {
                result = addQuestion(entry.getValue().getQuestion(), entry.getValue().getOptionA(), entry.getValue().getOptionB(),
                        entry.getValue().getOptionC(), entry.getValue().getOptionD(), entry.getValue().getCorrect());
            }
            if (result == 1) App.LOGGER.info("Questions reinserted to the database");
            else App.LOGGER.info("Question not reinserted");
            DbHandler.disconnect();
            AdminQuestionsPageController.questionsLoaded = false;
            onReturn();
        } else {
            App.LOGGER.info("Questions not deleted from database.");
            status.setText("Questions not deleted.");
            status.setTextFill(App.RED_COLOR);
        }

    }

    /*
     * Adds question to the database.
     */
    private int addQuestion(String question, String optionA, String optionB, String optionC, String optionD, String correct) {
        String sql = "INSERT INTO questions (question, optionA, optionB, optionC, optionD, correct) VALUES ('"
                + question + "', '" + optionA + "', '" + optionB + "', '" + optionC + "', '" + optionD + "', '" + correct + "')";
        return DbHandler.executeUpdate(sql);
    }

    /*
     * Checks if values are numeric
     */
    private boolean checkNumeric(String[] array) {
        App.LOGGER.info("Checking if values are numeric.");
        boolean numeric = true;
        for (String s : array) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                numeric = false;
                break;
            }
        }
        return numeric;
    }

    /*
     * Checks size of values and sign
     */
    private boolean checkSizeAndSign(String[] array) {
        int maxId = returnLastId();
        for (String s : array) {
            int id = Integer.parseInt(s);
            if (id > maxId || id <= 0) {
                App.LOGGER.info("Too high or negative value.");
                return false;
            }
        }
        return true;
    }

    /*
     * Returns last id from table
     */
    private int returnLastId() {
        String sql = "SELECT questionId FROM questions ORDER BY questionId DESC LIMIT 1";
        DbHandler.connect();
        int result = 0;
        try {
            ResultSet resultSet = DbHandler.executeQuery(sql);
            result = resultSet.getInt(1);
        } catch (SQLException e) {
            App.LOGGER.error("Error at retrieving last id from questions table. " + e.getMessage());
            DbHandler.disconnect();
        }
        DbHandler.disconnect();
        return result;
    }

    /*
     * Returns to previous page.
     */
    @FXML
    private void onReturn() {
        App.LOGGER.info("Closing deleteQuestions pop up window.");
        Stage stage = (Stage) returnButton.getScene().getWindow();
        stage.close();
    }

}

