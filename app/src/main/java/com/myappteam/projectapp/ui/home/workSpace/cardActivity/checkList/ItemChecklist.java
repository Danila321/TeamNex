package com.myappteam.projectapp.ui.home.workSpace.cardActivity.checkList;

public class ItemChecklist {
    private String id;
    private boolean checked;
    private String name;
    String date;

    public ItemChecklist() {

    }

    public ItemChecklist(String id, boolean checked, String name, String date) {
        this.id = id;
        this.checked = checked;
        this.name = name;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Boolean getChecked() {
        return checked;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}