package com.example.rush_b;

/**
 * Created by Deny on 2/25/2017.
 */
public class User {
    public String username;
    public String name;
    public String email;
    public int bombDefused;
    public int timeSpent;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.bombDefused = 0;
        this.timeSpent = 0;
    }
}

