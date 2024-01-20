package com.example.teamdraft.ui.homeui.boards;

public class Board {
    private String name;
    private String imageUri;
    private String date;
    private String editDate;
    private String code;

    public Board() {

    }

    public Board(String name, String imageUri, String date, String editDate, String code) {
        this.name = name;
        this.imageUri = imageUri;
        this.date = date;
        this.editDate = editDate;
        this.code = code;
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