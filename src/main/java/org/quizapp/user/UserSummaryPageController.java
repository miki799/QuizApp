package org.quizapp.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.quizapp.Answer;
import org.quizapp.App;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserSummaryPageController implements Initializable {

    @FXML
    Button finishButton;

    @FXML
    Label finalResult, correctAnswers, deathSentence;

    @FXML
    TableView<Answer> answersTable;

    @FXML
    private TableColumn<Answer, Integer> id, question, answer, correctAnswer;

    @FXML
    ObservableList<Answer> answerObservableList = FXCollections.observableArrayList(QuizPageController.answers);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        double correct = QuizPageController.correctAnswers, amount = QuizPageController.questionAmount;
        double result = (correct / amount) * 100;
        App.LOGGER.info("Correct answers: " + correct + " Question amount: " + amount);
        App.LOGGER.info("Final result: " + result + "%");
        finalResult.setText(finalResult.getText() + " " + result + "%");
        correctAnswers.setText(correctAnswers.getText() + " " + (int) correct + "/" + (int) amount);
        if (result >= 50.0) {
            deathSentence.setText("You have passed the quiz!");
            deathSentence.setTextFill(App.GREEN_COLOR);
        } else {
            deathSentence.setText("You didn't pass the quiz!");
            deathSentence.setTextFill(App.RED_COLOR);
        }

        userUpdate(QuizPageController.correctAnswers, QuizPageController.questionAmount);

        QuizPageController.correctAnswers = 0;
        QuizPageController.questionAmount = 0;
        QuizPageController.answers.clear();

    }

    public static void userUpdate(int correct, int amount) {
        DbHandler.connect();
        String username = UserLoginPageController.loggedUser;
        String sql = "UPDATE user SET result='" + correct + "', questionAmount='" + amount + "' WHERE username='" + username + "'";
        if (DbHandler.executeUpdate(sql) == 1) {
            App.LOGGER.info("User row updated");
        } else {
            App.LOGGER.warn("User row not updated");
        }
        DbHandler.disconnect();
    }

    /*
     *  Initializes TableView structure.
     */
    private void initTable() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        question.setCellValueFactory(new PropertyValueFactory<>("question"));
        answer.setCellValueFactory(new PropertyValueFactory<>("answer"));
        correctAnswer.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        answersTable.setItems(answerObservableList);
    }


    @FXML
    private void onFinish() {
        try {
            App.LOGGER.info("QUIZ FINISHED.");
            App.LOGGER.info("Going back to userMainPage");
            App.setRoot("userMainPage");
        } catch (IOException e) {
            App.LOGGER.error("Error at going back to userMainPage." + e.getMessage());
            e.printStackTrace();
        }
    }

}
