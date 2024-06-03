package com.example.teamdraft.ui.home.workSpace;

public class Card {
    private String id;
    private String name;
    private String description;

    public Card() {

    }

    public Card(String id, String name, String description) {
        this.id = id;
        this.description = description;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
