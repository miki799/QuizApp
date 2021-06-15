module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens org.quizapp to javafx.fxml;
    exports org.quizapp;
    exports org.quizapp.user;
    opens org.quizapp.user to javafx.fxml;
    exports org.quizapp.admin;
    opens org.quizapp.admin to javafx.fxml;
    exports org.quizapp.database;
    opens org.quizapp.database to javafx.fxml;
}