package com.example.yrmultimediaco.fileexplorer;

public class FolderCreatedEvent {

    private String folderName;

    public FolderCreatedEvent(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
