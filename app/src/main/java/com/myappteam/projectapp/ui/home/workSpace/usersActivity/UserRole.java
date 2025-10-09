package com.myappteam.projectapp.ui.home.workSpace.usersActivity;

import com.myappteam.projectapp.ui.home.workSpace.cardActivity.users.User;

public class UserRole {
    private User user;
    private String role;

    public UserRole() {

    }

    public UserRole(User user, String role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }
}
