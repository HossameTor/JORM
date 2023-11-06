package org.jorm;

import java.sql.*;

public interface DatabaseConnection {
    Connection connection = getConnection(AppConfig.getDatabaseURL(), AppConfig.getDatabaseUsername(), AppConfig.getDatabasePassword());
    private static Connection getConnection(String url, String username, String password){
        Connection con = null;
        try{
            con=DriverManager.getConnection(url, username, password);
        }catch(Exception e){
            System.out.println(e);
        }finally {
            return con;
        }
    }
}
