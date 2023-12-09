package com.example.teamdraft.ui.homeui.boards;

public class Board {
    private String name;
    private String date;
    private String code;

    public Board() {

    }

    public Board(String name, String date, String code) {
        this.name = name;
        this.date = date;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }
}