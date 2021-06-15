package org.quizapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.quizapp.database.DbHandler;

import java.io.IOException;

/**
 * QuizApp
 */
public class App extends Application {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Color GREEN_COLOR = Color.web("#0CCA4A");
    public static final Color RED_COLOR = Color.web("E84855");
    public static final int MAX_NUMBER_OF_QUESTIONS = 25;

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("mainPage"), 900, 600);
        stage.setTitle("QuizApp");
        stage.getIcons().add(new Image("question-mark.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // log4j2 setup
        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);
        DbHandler.createTables();
        LOGGER.info("QuizApp starts.");
        launch();
    }

}