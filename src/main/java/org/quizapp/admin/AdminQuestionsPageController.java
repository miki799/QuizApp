package org.quizapp.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.quizapp.App;
import org.quizapp.Question;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AdminQuestionsPageController {

    @FXML
    Button returnButton, loadButton, addButton, deleteButton, deleteAllButton;
    @FXML
    Label tableStatus;

    @FXML
    TableView<Question> questionsTable;
    @FXML
    private TableColumn<Question, Integer> questionId;
    @FXML
    private TableColumn<Question, String> question;
    @FXML
    private TableColumn<Question, String> optionA;
    @FXML
    private TableColumn<Question, String> optionB;
    @FXML
    private TableColumn<Question, String> optionC;
    @FXML
    private TableColumn<Question, String> optionD;
    @FXML
    private TableColumn<Question, String> correctAnswer;

    ObservableList<Question> questionList = FXCollections.observableArrayList();
    private Stage popUpScreen;
    public static List<Question> questions;
    public static boolean questionsLoaded;

    /*
     *  Initializes TableView structure.
     */
    private void initTable() {
        questionId.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        question.setCellValueFactory(new PropertyValueFactory<>("question"));
        optionA.setCellValueFactory(new PropertyValueFactory<>("optionA"));
        optionB.setCellValueFactory(new PropertyValueFactory<>("optionB"));
        optionC.setCellValueFactory(new PropertyValueFactory<>("optionC"));
        optionD.setCellValueFactory(new PropertyValueFactory<>("optionD"));
        correctAnswer.setCellValueFactory(new PropertyValueFactory<>("correct"));
    }

    /*
     * Loads questions from database.
     */
    @FXML
    private void onLoad() {
        tableStatus.setText("");
        App.LOGGER.info("Load button pressed");
        if (!questionsLoaded) {
            App.LOGGER.info("Loading questions from database...");
            initTable();
            questionList.clear(); // clears TableView
            int questionCount = 0;
            DbHandler.connect();
            ResultSet resultSet = DbHandler.executeQuery("SELECT * from questions");
            try {
                while (resultSet.next()) {
                    questionList.add(new Question(
                            resultSet.getInt("questionId"),
                            resultSet.getString("question"),
                            resultSet.getString("optionA"),
                            resultSet.getString("optionB"),
                            resultSet.getString("optionC"),
                            resultSet.getString("optionD"),
                            resultSet.getString("correct")
                    ));
                    questionsTable.setItems(questionList);
                    questionCount++;
                }
                tableStatus.setText("Number of questions loaded: " + questionCount);
                if (questionCount != 0) {
                    questionsLoaded = true;
                    tableStatus.setTextFill(App.GREEN_COLOR);
                } else tableStatus.setTextFill(App.RED_COLOR);
                App.LOGGER.info(questionCount + " questions loaded from database.");
            } catch (SQLException e) {
                App.LOGGER.warn("Error at loading questions from database.");
            }
            DbHandler.disconnect();
        }
    }

    /*
     * Going to addQuestionPage.
     */
    @FXML
    private void onAdd() {
        App.LOGGER.info("Going to addQuestionPage.");
        tableStatus.setText("");
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("addQuestion.fxml")));
            Scene scene = new Scene(parent);
            popUpScreen = new Stage();
            popUpScreen.setTitle("Add question");
            popUpScreen.getIcons().add(new Image("question-mark.png"));
            popUpScreen.setScene(scene);
            popUpScreen.initModality(Modality.APPLICATION_MODAL);
            popUpScreen.showAndWait(); // This forces the program to pay attention ONLY to this popup window until its closed
        } catch (IOException e) {
            App.LOGGER.error("Error at loading AddQuestion pop up window." + e.getMessage());
        }
        if (!questionsLoaded) onLoad();
    }

    /*
     * Going to delete question page.
     */
    @FXML
    private void onDelete() {
        if (questionsLoaded) {
            App.LOGGER.info("Going to deleteQuestions.");
            tableStatus.setText("");
            questions = questionsTable.getItems(); // zapisuje pytania do listy
            try {
                Parent parent = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("deleteQuestions.fxml")));
                Scene scene = new Scene(parent);
                popUpScreen = new Stage();
                popUpScreen.setTitle("Delete questions");
                popUpScreen.getIcons().add(new Image("question-mark.png"));
                popUpScreen.setScene(scene);
                popUpScreen.initModality(Modality.APPLICATION_MODAL);
                popUpScreen.showAndWait(); // This forces the program to pay attention ONLY to this popup window until its closed
            } catch (IOException e) {
                App.LOGGER.error("Error at loading deleteQuestions pop up window." + e.getMessage());
            }
            if (!questionsLoaded) onLoad();
        } else {
            App.LOGGER.info("Nothing to delete. Popup window won't be opened.");
            tableStatus.setText("Nothing to delete.");
            tableStatus.setTextFill(App.RED_COLOR);
        }
    }

    /*
     * Deletes all questions from table (deletes table and creates new one).
     */
    @FXML
    private void onDeleteAll() {
        DbHandler.connect();
        if (!DbHandler.tableExists("questions")) {
            App.LOGGER.info("Questions table doesn't exists, so there is nothing to delete.");
            tableStatus.setText("Nothing to delete.");
            tableStatus.setTextFill(App.RED_COLOR);
            DbHandler.disconnect();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your choice...");
        alert.setHeaderText("Are you sure you want to delete all of the questions?");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (questionsLoaded) {
                if (DbHandler.deleteTable("questions") == 0) {
                    App.LOGGER.info("Questions deleted.");
                    tableStatus.setText("Questions deleted.");
                    tableStatus.setTextFill(App.GREEN_COLOR);
                    if (DbHandler.createTable("questions") == 0) {
                        App.LOGGER.info("New questions table created.");
                    } else {
                        App.LOGGER.info("Error at creating new table");
                        DbHandler.disconnect();
                        return;
                    }
                    // clears TableView and sets boolean
                    questionList.clear();
                    questionsTable.setItems(questionList);
                    questionsLoaded = false;
                    DbHandler.disconnect();
                } else {
                    DbHandler.disconnect();
                    App.LOGGER.info("Questions not deleted from database.");
                    tableStatus.setText("Questions not deleted.");
                    tableStatus.setTextFill(App.RED_COLOR);
                }
            } else {
                App.LOGGER.info("Load questions before deletion.");
                tableStatus.setText("Nothing to delete.");
                tableStatus.setTextFill(App.RED_COLOR);
            }
        }
        DbHandler.disconnect();
    }

    /*
     * Going back to admin main page.
     */
    @FXML
    private void onReturn() {
        questionsLoaded=false;
        App.LOGGER.info("Going back to AdminMainPage.");
        try {
            App.setRoot("adminMainPage");
        } catch (IOException e) {
            App.LOGGER.warn("Error at going back to AdminMainPage." + e.getMessage());
        }
    }

}
