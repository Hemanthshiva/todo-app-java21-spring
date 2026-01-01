package com.example.todo.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getBaseUri() {
        return properties.getProperty("base.uri", "http://localhost");
    }
    
    public static int getPort() {
        return Integer.parseInt(properties.getProperty("port", "8080"));
    }
}