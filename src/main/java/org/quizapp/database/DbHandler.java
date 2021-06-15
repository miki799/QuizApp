package org.quizapp.database;

import org.quizapp.App;


import java.sql.*;

/**
 * Database handler class.
 */
public class DbHandler {

    private static final String connectionString = "jdbc:sqlite:QuizApp.db";
    private static Connection connection = null;
    private static Statement statement = null;


    //private static final String userAnswersTable = "";
    private static final String USER_TABLE = "CREATE TABLE IF NOT EXISTS user(name VARCHAR, lastname VARCHAR, username VARCHAR, password VARCHAR, result INTEGER, questionAmount INTEGER, PRIMARY KEY(username))";
    private static final String ADMIN_TABLE = "CREATE TABLE IF NOT EXISTS admin(adminName VARCHAR, adminPassword VARCHAR, PRIMARY KEY(adminName))";
    private static final String QUESTION_TABLE = "CREATE TABLE IF NOT EXISTS questions(" +
            "questionId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "question VARCHAR NOT NULL," +
            "optionA VARCHAR NOT NULL," +
            "optionB VARCHAR NOT NULL," +
            "optionC VARCHAR NOT NULL," +
            "optionD VARCHAR NOT NULL," +
            "correct VARCHAR NOT NULL" +
            ")";

    /**
     * Establishes connection with QuizApp database.
     */
    public static void connect() {
        try {
            connection = DriverManager.getConnection(connectionString);
            statement = connection.createStatement();
            App.LOGGER.info("Connection ESTABLISHED.");
        } catch (SQLException e) {
            App.LOGGER.warn("Connection NOT ESTABLISHED: " + e.getMessage());
        }
    }

    /**
     * Ends connection with database.
     */
    public static void disconnect() {
        if (connection != null) {
            try {
                statement.close();
                statement = null;
                connection.close();
                connection = null;
            } catch (SQLException e) {
                App.LOGGER.warn("Connection NOT CLOSED: " + e.getMessage());
            }
            App.LOGGER.info("Connection CLOSED");
        }
    }

    /**
     * Executes Update.
     *
     * @param sql SQL statement
     * @return row count for DML statements or 0 for statements that return nothing
     */
    public static int executeUpdate(String sql) {
        int i = 0;
        try {
            i = statement.executeUpdate(sql);
            App.LOGGER.info("Update executed.");
        } catch (SQLException e) {
            App.LOGGER.warn("Update not executed: " + e.getMessage());
        }
        return i;
    }

    /**
     * Executes query.
     *
     * @param sql SQL query
     * @return data produced by the given query
     */
    public static ResultSet executeQuery(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            App.LOGGER.info("Query executed.");
        } catch (SQLException e) {
            App.LOGGER.warn("Query not executed: " + e.getMessage());
        }
        return resultSet;
    }

    /**
     * Insert root account credentials.
     */
    public static void addRootAdmin() {
        String sql = "INSERT OR IGNORE INTO admin VALUES ('root', 'toor')";
        executeUpdate(sql);
    }

    /**
     * Creates every table.
     */
    public static void createTables() {
        connect();
        executeUpdate(ADMIN_TABLE);
        addRootAdmin();
        executeUpdate(USER_TABLE);
        executeUpdate(QUESTION_TABLE);
        disconnect();
        App.LOGGER.info("Tables created.");
    }

    public static int deleteTable(String tableName) {
        int result = executeUpdate("DROP TABLE IF EXISTS " + tableName);
        if (result == 0) {
            App.LOGGER.info("Table has been deleted: " + tableName);
        } else {
            App.LOGGER.warn("Table deletion error: " + tableName);
        }
        return result;
    }

    public static int createTable(String tableName) {
        int result = executeUpdate(QUESTION_TABLE);
        if (result == 0) {
            App.LOGGER.info("Table has been created: " + tableName);
        } else {
            App.LOGGER.warn("Table creation error: " + tableName);
        }
        return result;
    }

    /*
     * Checks if table exists by moving cursor to the first row. May introduce false results when table is missing first row.
     */
    public static boolean tableExists(String tableName) {
        boolean doesExist = false;
        try{
            ResultSet resultSet = executeQuery("SELECT * FROM " + tableName + " where questionId=1");
            if (resultSet.next()) {
                doesExist = true;
            }
        } catch (SQLException e) {
            App.LOGGER.error("Error at checking if table exists." + e.getMessage());
        }
        return doesExist;
    }

    /**
     * Prints tables. Remember to change attribute names.
     *
     * @param tableName Name of database table.
     */
    public static void printTable(String tableName) {
        try {
            String sql = "SELECT * FROM " + tableName;
            ResultSet resultSet = executeQuery(sql);
            while (resultSet.next()) {
                if (tableName.equals("user")) {
                    System.out.printf("%s %s %s %s %d\n", resultSet.getString("name"), resultSet.getString("lastname"),
                            resultSet.getString("username"), resultSet.getString("password"), resultSet.getInt("result"));
                }
            }
        } catch (SQLException e) {
            App.LOGGER.warn("printTable() error: " + e.getMessage());
        }
    }
}
