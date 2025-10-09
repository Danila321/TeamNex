package com.myappteam.projectapp.ui.home.workSpace;

public class Item {
    private String id;
    private String name;

    public Item() {

    }

    public Item(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
