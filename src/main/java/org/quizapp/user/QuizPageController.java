package org.quizapp.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.quizapp.Answer;
import org.quizapp.App;
import org.quizapp.Question;
import org.quizapp.database.DbHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class QuizPageController implements Initializable {

    @FXML
    Button endButton, nextButton;

    @FXML
    RadioButton optionA, optionB, optionC, optionD;

    @FXML
    Label questionLabel, status, questionNumber;

    @FXML
    ToggleGroup answersGroup;

    public List<Question> questions = new ArrayList<>();
    public static List<Answer> answers = new ArrayList<>();
    public static int questionAmount;
    public static int correctAnswers;
    private int i; // question counter
    private String userAnswer = "";

    /*
     * Initializes quiz (loads question etc.)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        App.LOGGER.info("Quiz initialization (loading question)");
        DbHandler.connect();
        ResultSet resultSet = DbHandler.executeQuery("SELECT * FROM questions");
        try {
            while (resultSet.next()) {
                Question loadedQuestion = new Question(
                        resultSet.getInt("questionId"), resultSet.getString("question"),
                        resultSet.getString("optionA"), resultSet.getString("optionB"),
                        resultSet.getString("optionC"), resultSet.getString("optionD"),
                        resultSet.getString("correct")
                );
                questions.add(loadedQuestion);
            }
        } catch (SQLException e) {
            App.LOGGER.error("Error at loading questions from database." + e.getMessage());
            e.printStackTrace();
        }
        DbHandler.disconnect();
        App.LOGGER.info("Questions loaded from database.");

        // question shuffles
        Collections.shuffle(questions, new Random());

        // load first question
        loadQuestion();
    }


    private void loadQuestion() {
        Question question = questions.get(i);
        // shuffling answers
        List<String> answerArray = Arrays.asList(
                question.getOptionA(), question.getOptionB(),
                question.getOptionC(), question.getOptionD()
        );
        Collections.shuffle(answerArray, new Random());
        questionLabel.setText(question.getQuestion());
        //optionA.setText(question.getOptionA());
        optionA.setText(answerArray.get(0));
        optionA.setSelected(false);
        //optionB.setText(question.getOptionB());
        optionB.setText(answerArray.get(1));
        optionB.setSelected(false);
        //optionC.setText(question.getOptionC());
        optionC.setText(answerArray.get(2));
        optionC.setSelected(false);
        //optionD.setText(question.getOptionD());
        optionD.setText(answerArray.get(3));
        optionD.setSelected(false);
        App.LOGGER.info("Question number" + (i + 1) + " loaded to the screen.");
        questionNumber.setText(i + 1 + "/" + questionAmount);
        i++;
    }

    /*
     * Checks answer, adds it to the list etc.
     */
    private void checker() {

        if (questions.get(i - 1).getCorrect().equals(userAnswer)) {
            correctAnswers++;
        }

        // adding answer to list
        Answer answer = new Answer(i, questions.get(i - 1).getQuestion(), userAnswer, questions.get(i - 1).getCorrect());
        answers.add(answer);

        userAnswer = "";

        if (i < questionAmount) {
            if (i == questionAmount - 1) {
                nextButton.setText("FINISH QUIZ");
            }
            loadQuestion();
        } else {
            try {
                App.LOGGER.info("Going to summaryPage");
                App.setRoot("userSummaryPage");
            } catch (IOException e) {
                App.LOGGER.error("Error at loading summaryPage." + e.getMessage());
            }
        }

    }

    @FXML
    private void onEnd() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm your choice");
        alert.setHeaderText("Are you sure you want to end? Progress will be lost...");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            App.LOGGER.info("User quits quiz! Results are reseted.");
            // reset
            answers.clear();
            questionAmount = 0;
            correctAnswers = 0;
            UserSummaryPageController.userUpdate(correctAnswers, questionAmount);
            try {
                App.LOGGER.info("Going back to userMainPage");
                App.setRoot("userMainPage");
            } catch (IOException e) {
                App.LOGGER.error("Error at going back to userMainPage." + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*
     * Invokes checker function.
     */
    @FXML
    private void onNext() {
        if (!userAnswer.isEmpty()) {
            status.setText("");
            checker();
        } else {
            App.LOGGER.info("Answer not selected");
            status.setText("Answer not selected.");
            status.setTextFill(App.RED_COLOR);
        }
    }

    /*
     * Gets answer from RadioButton.
     */
    @FXML
    private void onA() {
        userAnswer = optionA.getText();
        App.LOGGER.info("Answer A selected");
    }

    @FXML
    private void onB() {
        userAnswer = optionB.getText();
        App.LOGGER.info("Answer B selected");
    }

    /*
     * Gets answer from RadioButton.
     */
    @FXML
    private void onC() {
        userAnswer = optionC.getText();
        App.LOGGER.info("Answer C selected");
    }

    /*
     * Gets answer from RadioButton.
     */
    @FXML
    private void onD() {
        userAnswer = optionD.getText();
        App.LOGGER.info("Answer D selected");
    }
}
