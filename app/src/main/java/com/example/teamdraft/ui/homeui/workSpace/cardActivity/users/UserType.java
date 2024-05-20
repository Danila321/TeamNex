package com.example.teamdraft.ui.homeui.workSpace.cardActivity.users;

public class UserType {
    private User user;
    private boolean type;

    public UserType() {

    }

    public UserType(User user, boolean type) {
        this.user = user;
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public boolean getType() {
        return type;
    }
}
