package com.example.teamdraft.ui.homeui.workSpace;

public class Card {
    private String id;
    private String name;
    private String deadline;

    public Card() {

    }

    public Card(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Card(String id, String name, String deadline) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }
}
