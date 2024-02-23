package com.example.teamdraft.ui.homeui.workSpace;

public class Card {
    private String name;
    private String deadline;

    public Card(){

    }

    public Card(String name){
        this.name = name;
    }

    public Card(String name, String deadline){
        this.name = name;
        this.deadline = deadline;
    }

    public String getName(){
        return name;
    }

    public String getDeadline(){
        return deadline;
    }
}
