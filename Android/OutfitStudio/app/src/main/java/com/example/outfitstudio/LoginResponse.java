package com.example.outfitstudio;

public class LoginResponse {

    private boolean success;
    private String message;
    private int user_id;
    private String name;
    private String email;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}