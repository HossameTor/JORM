package org.jorm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();

    static {
        try {
            FileInputStream input = new FileInputStream("config.properties");
            properties.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDatabaseURL() {
        return properties.getProperty("db.url");
    }

    public static String getDatabaseUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return properties.getProperty("db.password");
    }
}

