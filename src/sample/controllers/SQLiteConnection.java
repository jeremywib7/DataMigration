package sample.controllers;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteConnection {

    public static Connection Connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:datamigration.sqlite");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            return null;
        }
    }
}