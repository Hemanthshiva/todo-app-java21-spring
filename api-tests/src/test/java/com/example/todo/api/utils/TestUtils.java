package com.example.todo.api.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {
    public static String generateRandomUsername() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
    public static String generateRandomEmail() {
        return generateRandomUsername() + "@example.com";
    }
    
    public static Integer extractTodoIdFromHtml(String html, String description) {
        // Look for the description and then find the update button with data-testid containing the ID
        int descIndex = html.indexOf(">" + description + "<");
        if (descIndex == -1) return null;
        
        String afterDesc = html.substring(descIndex);
        Pattern pattern = Pattern.compile("data-testid=\"todo-edit-button-(\\d+)\"");
        Matcher matcher = pattern.matcher(afterDesc);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
    
    public static Long extractAssignmentIdFromHtml(String html, String description) {
         // Look for the description and then find the accept button call
         int descIndex = html.indexOf(">" + description + "<");
         if (descIndex == -1) return null;
         
         String afterDesc = html.substring(descIndex);
         // onclick="openAcceptModal(123)"
         Pattern pattern = Pattern.compile("openAcceptModal\\((\\d+)\\)");
         Matcher matcher = pattern.matcher(afterDesc);
         if (matcher.find()) {
             return Long.parseLong(matcher.group(1));
         }
         return null;
    }
}