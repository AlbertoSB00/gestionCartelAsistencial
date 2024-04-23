package com.example.gestioncartelasistencial;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSQL {
    protected static String DB = "appandujarsalud";
    protected static String IP = "192.168.1.10";
    protected static String PORT = "3306";
    protected static String USERNAME = "root";
    protected static String PASSWORD = "usuario";

    public Connection CONN() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB;
            connection = DriverManager.getConnection(connectionString, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            Log.e("ConnectionSQL", "ClassNotFoundException: " + e.getMessage());
        } catch (SQLException e) {
            Log.e("ConnectionSQL", "SQLException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("ConnectionSQL", "Exception: " + e.getMessage());
        }

        return connection;
    }
}
