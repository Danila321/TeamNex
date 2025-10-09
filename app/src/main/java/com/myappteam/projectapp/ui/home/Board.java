package com.myappteam.projectapp.ui.home;

public class Board {
    private String id;
    private String name;
    private String imageUri;
    private String date;
    private String editDate;
    private String code;

    public Board() {

    }

    public Board(String id, String name, String imageUri, String date, String editDate, String code) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.date = date;
        this.editDate = editDate;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getDate() {
        return date;
    }

    public String getEditDate() {
        return editDate;
    }

    public String getCode() {
        return code;
    }
}