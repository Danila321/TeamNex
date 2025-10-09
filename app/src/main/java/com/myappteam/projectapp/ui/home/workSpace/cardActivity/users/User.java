package com.myappteam.projectapp.ui.home.workSpace.cardActivity.users;

public class User {
    private String id;
    private String name;
    private String photo;

    public User() {

    }

    public User(String id, String name, String photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }
}
