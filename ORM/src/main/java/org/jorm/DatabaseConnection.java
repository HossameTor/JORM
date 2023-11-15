package org.jorm;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private Connection conn;

    private DatabaseConnection() {
        initializeConnection();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(AppConfig.getDatabaseURL(),AppConfig.getDatabaseUsername(),AppConfig.getDatabasePassword());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
