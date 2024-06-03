package com.example.teamdraft.ui.home.workSpace.cardActivity.attachments;

public class ItemAttachment {
    private String id;
    private String file;
    private String fileType;
    private String name;
    private String date;

    public ItemAttachment() {

    }

    public ItemAttachment(String id, String file, String fileType, String name, String date) {
        this.id = id;
        this.file = file;
        this.fileType = fileType;
        this.name = name;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public String getFileType() {
        return fileType;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
