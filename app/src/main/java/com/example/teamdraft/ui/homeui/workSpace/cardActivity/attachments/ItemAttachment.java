package com.example.teamdraft.ui.homeui.workSpace.cardActivity.attachments;

public class ItemAttachment {
    private String id;
    private String image;
    private String name;
    private String date;

    public ItemAttachment() {

    }

    public ItemAttachment(String id, String image, String name, String date) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
